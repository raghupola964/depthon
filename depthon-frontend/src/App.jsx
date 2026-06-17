import { useState, useEffect } from "react";
import PostForm from "./PostForm";
import Login from "./Login";
import Signup from "./Signup";
import Settings from "./Settings";
import { getToken, clearToken, isLoggedIn } from "./auth";

import { TAXONOMY } from "./taxonomy";

import MyPosts from "./MyPosts";

function App() {
  const [loggedIn, setLoggedIn] = useState(isLoggedIn());  // are we logged in?
  const [authScreen, setAuthScreen] = useState("login");   // "login" or "signup"

  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [view, setView] = useState("feed");   // "feed" or "settings"
  function labelForSubdivision(subValue) {
    for (const divKey of Object.keys(TAXONOMY)) {
      const found = TAXONOMY[divKey].subdivisions.find((s) => s.value === subValue);
      if (found) return found.label;
    }
    return subValue;
  }
  const [reportMsg, setReportMsg] = useState({});  // per-post report messages

  function reportPost(postId) {
    fetch("http://localhost:8080/api/posts/" + postId + "/report", {
      method: "POST",
      headers: { "Authorization": "Bearer " + getToken() },
    })
      .then(async (r) => {
        const data = await r.json();
        if (!r.ok) throw new Error(data.error || "Could not report");
        return data;
      })
      .then((data) => {
        // show the server's message under this post
        setReportMsg((prev) => ({ ...prev, [postId]: data.message }));
        loadFeed();  // refresh — if it just got hidden, it'll disappear
      })
      .catch((err) => {
        setReportMsg((prev) => ({ ...prev, [postId]: err.message }));
      });
  }

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

  // Poll the feed every 5 seconds so posts judged in the background appear automatically
  useEffect(() => {
    if (!loggedIn || view !== "feed") return;
    const interval = setInterval(() => {
      loadFeed();
    }, 5000);
    return () => clearInterval(interval);  // stop polling when leaving feed / logging out
  }, [loggedIn, view]);

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
// ---- LOGGED IN ----
  return (
    <div className="min-h-screen bg-[#0a0a0b] text-zinc-100">
      <div className="mx-auto max-w-2xl px-5 py-10">

        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-5 mb-8">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-white to-zinc-500 text-base font-bold text-black">D</div>
            <span className="text-xl font-bold tracking-tight">Depthon</span>
          </div>
          <div className="flex items-center gap-4">
            <button
              onClick={() => setView("myposts")}
              className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors"
            >
              My Posts
            </button>
            <button
              onClick={() => setView(view === "feed" ? "settings" : "feed")}
              className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors"
            >
              {view === "feed" ? "Settings" : "Feed"}
            </button>
            <button
              onClick={handleLogout}
              className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors"
            >
              Log out
            </button>
          </div>
        </div>

        {/* Settings view */}
        {view === "settings" && <Settings onBack={() => { setView("feed"); loadFeed(); }} />}
        {view === "myposts" && <MyPosts onBack={() => { setView("feed"); loadFeed(); }} />}
        {/* Feed view */}
        {view === "feed" && (
          <>
            <PostForm onPostJudged={loadFeed} />
            <div className="text-xs uppercase tracking-wider text-zinc-600 mb-4">Your Feed</div>
            {loading && <p className="text-sm text-zinc-500">Loading your feed...</p>}
            {error && <p className="text-sm text-red-400">{error}</p>}
            {!loading && !error && posts.length === 0 && (
              <p className="text-sm text-zinc-600">No posts in your field yet. Be the first to share something.</p>
            )}
            <div className="space-y-3">
              {posts.map((post) => (
                <div
                  key={post.id}
                  className="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur-md transition-all hover:border-white/30 hover:bg-white/[0.07] animate-fadeUp"
                >
                  <h2 className="text-base font-semibold tracking-tight mb-2">{post.title}</h2>
                  <p className="text-sm leading-relaxed text-zinc-400 mb-3">{post.content}</p>
                  <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="h-5 w-5 rounded-full border border-white/10 bg-gradient-to-br from-zinc-600 to-zinc-900"></div>
                  <span className="text-xs text-zinc-500">{post.authorUsername}</span>
                </div>
                <div className="flex items-center gap-3">
                  {post.subdivision && (
                    <span className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-xs text-zinc-400">
                      {labelForSubdivision(post.subdivision)}
                    </span>
                  )}
                  <button
                    onClick={() => reportPost(post.id)}
                    className="text-xs text-zinc-600 hover:text-red-400 transition-colors"
                    title="Report as wrong field"
                  >
                    Report
                  </button>
                </div>
              </div>
              {reportMsg[post.id] && (
                <p className="mt-2 text-xs text-amber-400/80">{reportMsg[post.id]}</p>
              )}
                </div>
              ))}
            </div>
          </>
        )}

      </div>
    </div>
  );
}

export default App;