import os
from typing import TypedDict
from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langgraph.graph import StateGraph, END

load_dotenv()  # reads .env so GOOGLE_API_KEY is available


# ----- The state that flows through the graph -----

class JudgeState(TypedDict):
    title: str
    content: str
    insight_score: float
    decision: str
    feedback: str


# ----- The LLM (the reasoning engine) -----

llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    temperature=0,  # 0 = consistent, deterministic judging
)


# ----- Node 1: free pre-checks (no AI cost) -----

def pre_check(state: JudgeState) -> JudgeState:
    content = state["content"].strip()

    if len(content) < 50:
        state["decision"] = "REJECTED"
        state["insight_score"] = 0.0
        state["feedback"] = "Content under 50 characters. Depthon is for depth."
    return state


# ----- Node 2: the AI judge -----

JUDGE_PROMPT = """You are the Gatekeeper of Depthon, a professional network \
where ONLY high-insight-density posts are allowed. No fluff, no engagement \
bait, no motivational filler.

Evaluate this post:

TITLE: {title}
CONTENT: {content}

Score its INSIGHT DENSITY from 0 to 10:
- 9-10: Rare expertise. Specific numbers, hard-won lessons, reproducible details.
- 7-8: Solid professional insight. Concrete and useful.
- 4-6: Has a point but vague. Needs specifics, evidence, or depth.
- 0-3: Fluff, bait, generic advice, or off-topic.

Reply in EXACTLY this format (two lines):
SCORE: <number>
FEEDBACK: <one or two sentences: if score < 7, coach the author on what \
specific improvements would get it approved>"""


def ai_judge(state: JudgeState) -> JudgeState:
    prompt = JUDGE_PROMPT.format(title=state["title"], content=state["content"])
    response = llm.invoke(prompt)
    text = response.content

    # Parse the two-line reply
    score = 5.0
    feedback = "Could not parse judge response."
    for line in text.strip().splitlines():
        if line.upper().startswith("SCORE:"):
            try:
                score = float(line.split(":", 1)[1].strip())
            except ValueError:
                pass
        elif line.upper().startswith("FEEDBACK:"):
            feedback = line.split(":", 1)[1].strip()

    state["insight_score"] = score
    state["feedback"] = feedback
    return state


# ----- Node 3: turn the score into a verdict -----

def verdict(state: JudgeState) -> JudgeState:
    score = state["insight_score"]
    if score >= 7:
        state["decision"] = "APPROVED"
    elif score >= 4:
        state["decision"] = "NEEDS_REVISION"
    else:
        state["decision"] = "REJECTED"
    return state


# ----- Routing: skip the AI if pre_check already rejected -----

def route_after_precheck(state: JudgeState) -> str:
    if state.get("decision") == "REJECTED":
        return "end"        # already rejected for free - skip the AI
    return "ai_judge"


# ----- Build the graph -----

graph = StateGraph(JudgeState)
graph.add_node("pre_check", pre_check)
graph.add_node("ai_judge", ai_judge)
graph.add_node("verdict", verdict)

graph.set_entry_point("pre_check")
graph.add_conditional_edges("pre_check", route_after_precheck,
                            {"ai_judge": "ai_judge", "end": END})
graph.add_edge("ai_judge", "verdict")
graph.add_edge("verdict", END)

gatekeeper = graph.compile()


# ----- Public function main.py will call -----

def judge_post(title: str, content: str) -> dict:
    result = gatekeeper.invoke({
        "title": title,
        "content": content,
        "insight_score": 0.0,
        "decision": "",
        "feedback": "",
    })
    return {
        "decision": result["decision"],
        "insight_score": result["insight_score"],
        "feedback": result["feedback"],
    }