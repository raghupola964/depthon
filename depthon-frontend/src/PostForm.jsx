import { useState } from "react";

import { getToken } from "./auth";

function PostForm({ onPostJudged }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [verdict, setVerdict] = useState(null);

  function handleSubmit() {
    setSubmitting(true);
    setVerdict(null);

    fetch("http://localhost:8080/api/posts", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + getToken(),
      },
      body: JSON.stringify({ title, content}),
    })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || "Request failed");
        return data;
      })
      .then((data) => {
        setVerdict(data);
        setSubmitting(false);
        setTitle("");
        setContent("");
        if (onPostJudged) onPostJudged();
      })
      .catch((err) => {
        setVerdict({ status: "BLOCKED", feedback: err.message });
        setSubmitting(false);
      });
  }

  const isPending = verdict?.status === "PENDING";
  const isBlocked = verdict?.status === "BLOCKED";
  const verdictColor = isPending
    ? "border-blue-500/40 bg-blue-500/10 text-blue-300"
    : "border-red-500/40 bg-red-500/10 text-red-300";

  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur-md mb-8">
      <div className="text-sm font-medium text-zinc-400 mb-3">Write a post</div>

      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Title"
        className="w-full rounded-lg border border-white/10 bg-black/30 px-3 py-2.5 text-sm text-zinc-100 placeholder-zinc-600 outline-none transition-colors focus:border-white/30 mb-2.5"
      />

      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="Your insight (at least 50 characters)..."
        rows={4}
        className="w-full resize-none rounded-lg border border-white/10 bg-black/30 px-3 py-2.5 text-sm text-zinc-100 placeholder-zinc-600 outline-none transition-colors focus:border-white/30 mb-3"
      />

      <button
        onClick={handleSubmit}
        disabled={submitting}
        className="rounded-lg bg-zinc-100 px-5 py-2.5 text-sm font-semibold text-black transition-all hover:bg-white active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {submitting ? "Submitting..." : "Submit to the Gatekeeper"}      </button>

      {verdict && (
        <div className={`mt-4 rounded-xl border px-4 py-3 animate-fadeUp ${verdictColor}`}>
          {isPending ? (
            <>
              <div className="text-xs font-bold tracking-wider mb-1">● UNDER REVIEW</div>
              <p className="text-sm leading-relaxed text-zinc-400">
                Your post has been submitted and the Gatekeeper is reviewing it. It will appear in the feed once approved.
              </p>
            </>
          ) : (
            <>
              <div className="text-xs font-bold tracking-wider mb-1">● {verdict.status}</div>
              <p className="text-sm leading-relaxed text-zinc-400">{verdict.feedback}</p>
            </>
          )}
        </div>
      )}
    </div>
  );
}

export default PostForm;