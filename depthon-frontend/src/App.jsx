import { useState, useEffect } from "react";
import PostForm from "./PostForm";
import Login from "./Login";
import Signup from "./Signup";
import { getToken, clearToken, isLoggedIn } from "./auth";

function App() {
  const [loggedIn, setLoggedIn] = useState(isLoggedIn());  // are we logged in?
  const [authScreen, setAuthScreen] = useState("login");   // "login" or "signup"

  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  function loadFeed() {
    fetch("http://localhost:8080/api/posts/feed/mine", {
      headers: { "Authorization": "Bearer " + getToken() },  // attach the token
    })
      .then(async (response) => {
        if (response.status === 401 || response.status === 403) {
          // token missing or expired -> log out
          handleLogout();
          throw new Error("Session expired. Please log in again.");
        }
        const data = await response.json();
        if (!response.ok) throw new Error("Could not load feed");
        return data;
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

  // Load the feed whenever we become logged in
  useEffect(() => {
    if (loggedIn) loadFeed();
  }, [loggedIn]);

  function handleLogout() {
    clearToken();
    setLoggedIn(false);
    setPosts([]);
  }

  // ---- NOT LOGGED IN: show login or signup ----
  if (!loggedIn) {
    if (authScreen === "login") {
      return (
        <div className="min-h-screen bg-[#0a0a0b] text-zinc-100">
          <Login
            onLoggedIn={() => setLoggedIn(true)}
            onSwitchToSignup={() => setAuthScreen("signup")}
          />
        </div>
      );
    }
    return (
      <div className="min-h-screen bg-[#0a0a0b] text-zinc-100">
        <Signup
          onSignedUp={() => setLoggedIn(true)}
          onSwitchToLogin={() => setAuthScreen("login")}
        />
      </div>
    );
  }

  // ---- LOGGED IN: show the feed ----
  return (
    <div className="min-h-screen bg-[#0a0a0b] text-zinc-100">
      <div className="mx-auto max-w-2xl px-5 py-10">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-5 mb-8">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-white to-zinc-500 text-base font-bold text-black">D</div>
            <span className="text-xl font-bold tracking-tight">Depthon</span>
          </div>
          <button
            onClick={handleLogout}
            className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors"
          >
            Log out
          </button>
        </div>

        {/* Post form */}
        <PostForm onPostJudged={loadFeed} />

        {/* Feed label */}
        <div className="text-xs uppercase tracking-wider text-zinc-600 mb-4">Your Feed</div>

        {/* States */}
        {loading && <p className="text-sm text-zinc-500">Loading your feed...</p>}
        {error && <p className="text-sm text-red-400">{error}</p>}
        {!loading && !error && posts.length === 0 && (
          <p className="text-sm text-zinc-600">No posts in your field yet. Be the first to share something.</p>
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