from fastapi import FastAPI
from pydantic import BaseModel
from agent import judge_post as run_gatekeeper

app = FastAPI(
    title="Depthon AI Gatekeeper",
    description="The judge that decides which posts deserve the feed",
    version="0.1.0",
)


# ----- DTOs (pydantic models) -----

class PostToJudge(BaseModel):
    post_id: int
    title: str
    content: str


class Verdict(BaseModel):
    post_id: int
    decision: str          # APPROVED | REJECTED | NEEDS_REVISION
    feedback: str
    insight_score: float   # 0.0 to 10.0


# ----- Endpoints -----

@app.get("/health")
def health_check():
    return {"status": "alive", "service": "depthon-ai-gatekeeper"}


@app.post("/judge", response_model=Verdict)
def judge_post(post: PostToJudge):
    result = run_gatekeeper(post.title, post.content)
    return Verdict(
        post_id=post.post_id,
        decision=result["decision"],
        feedback=result["feedback"],
        insight_score=result["insight_score"],
    )