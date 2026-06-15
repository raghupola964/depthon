import { useState, useEffect } from "react";
import { getToken } from "./auth";
import { TAXONOMY } from "./taxonomy";

function MyPosts({ onBack }) {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/posts/mine", {
      headers: { "Authorization": "Bearer " + getToken() },
    })
      .then(async (r) => {
        const data = await r.json();
        if (!r.ok) throw new Error("Could not load your posts");
        return data;
      })
      .then((data) => { setPosts(data); setLoading(false); })
      .catch((e) => { setError(e.message); setLoading(false); });
  }, []);

  function labelForSubdivision(subValue) {
    if (!subValue) return null;
    for (const divKey of Object.keys(TAXONOMY)) {
      const found = TAXONOMY[divKey].subdivisions.find((s) => s.value === subValue);
      if (found) return found.label;
    }
    return subValue;
  }

  // a small colored badge per status
  function statusBadge(status) {
    const colors = {
      APPROVED: "text-emerald-400 border-emerald-400/30 bg-emerald-400/10",
      REJECTED: "text-red-400 border-red-400/30 bg-red-400/10",
      PENDING: "text-amber-400 border-amber-400/30 bg-amber-400/10",
      NEEDS_REVISION: "text-blue-400 border-blue-400/30 bg-blue-400/10",
    };
    const cls = colors[status] || "text-zinc-400 border-white/10 bg-white/5";
    return (
      <span className={"rounded-full border px-2.5 py-1 text-xs " + cls}>
        {status}
      </span>
    );
  }

  return (
    <div className="mx-auto max-w-2xl px-5 py-10">
      <div className="flex items-center justify-between border-b border-white/10 pb-5 mb-8">
        <span className="text-xl font-bold tracking-tight">My Posts</span>
        <button onClick={onBack} className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors">
          Back to feed
        </button>
      </div>

      {loading && <p className="text-sm text-zinc-500">Loading your posts...</p>}
      {error && <p className="text-sm text-red-400">{error}</p>}
      {!loading && !error && posts.length === 0 && (
        <p className="text-sm text-zinc-600">You haven't posted anything yet.</p>
      )}

      <div className="space-y-3">
        {posts.map((post) => (
          <div
            key={post.id}
            className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur-md"
          >
            <div className="flex items-start justify-between gap-3 mb-2">
              <h2 className="text-base font-semibold tracking-tight">{post.title}</h2>
              {statusBadge(post.status)}
            </div>
            <p className="text-sm leading-relaxed text-zinc-400 mb-3">{post.content}</p>

            {/* feedback from the AI, if any */}
            {post.moderationFeedback && (
              <p className="text-xs text-zinc-500 italic mb-2 border-l-2 border-white/10 pl-3">
                Gatekeeper: {post.moderationFeedback}
              </p>
            )}

            <div className="flex items-center gap-2">
              {post.subdivision && (
                <span className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-xs text-zinc-400">
                  {labelForSubdivision(post.subdivision)}
                </span>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MyPosts;