import { useState, useEffect } from "react";
import PostForm from "./PostForm";

function App() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  function loadFeed() {
    fetch("http://localhost:8080/api/posts/feed")
      .then((response) => {
        if (!response.ok) throw new Error("Feed request failed: " + response.status);
        return response.json();
      })
      .then((data) => {
        setPosts(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }

  useEffect(() => {
    loadFeed();
  }, []);

  return (
    <div className="min-h-screen bg-[#0a0a0b]">
      <div className="mx-auto max-w-2xl px-5 py-10">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-5 mb-8">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-white to-zinc-500 text-base font-bold text-black">D</div>
            <span className="text-xl font-bold tracking-tight">Depthon</span>
          </div>
          <span className="text-xs uppercase tracking-wider text-zinc-500">No Fluff</span>
        </div>

        {/* Post form */}
        <PostForm onPostJudged={loadFeed} />

        {/* Feed label */}
        <div className="text-xs uppercase tracking-wider text-zinc-600 mb-4">The Feed</div>

        {/* States */}
        {loading && <p className="text-sm text-zinc-500">Loading the feed...</p>}
        {error && <p className="text-sm text-red-400">Error: {error}</p>}
        {!loading && !error && posts.length === 0 && (
          <p className="text-sm text-zinc-600">No approved posts yet. The feed is empty.</p>
        )}

        {/* Posts */}
        <div className="space-y-3">
          {posts.map((post) => (
            <div
              key={post.id}
              className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur-md transition-all hover:border-white/30 hover:bg-white/[0.07] animate-fadeUp"
            >
              <h2 className="text-base font-semibold tracking-tight mb-2">{post.title}</h2>
              <p className="text-sm leading-relaxed text-zinc-400 mb-3">{post.content}</p>
              <div className="flex items-center gap-2">
                <div className="h-5 w-5 rounded-full border border-white/10 bg-gradient-to-br from-zinc-600 to-zinc-900"></div>
                <span className="text-xs text-zinc-500">{post.authorUsername}</span>
              </div>
            </div>
          ))}
        </div>

      </div>
    </div>
  );
}

export default App;