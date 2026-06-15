import { useState, useEffect } from "react";
import { getToken } from "./auth";
import { TAXONOMY } from "./taxonomy";

function Settings({ onBack }) {
  const [data, setData] = useState(null);      // the /follows response
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  // Load current follow status
  function loadFollows() {
    fetch("http://localhost:8080/api/settings/follows", {
      headers: { "Authorization": "Bearer " + getToken() },
    })
      .then(async (r) => {
        const d = await r.json();
        if (!r.ok) throw new Error(d.error || "Could not load settings");
        return d;
      })
      .then((d) => setData(d))
      .catch((e) => setError(e.message));
  }

  useEffect(() => { loadFollows(); }, []);

  function follow(subValue) {
    setBusy(true);
    setError(null);
    fetch("http://localhost:8080/api/settings/follow", {
      method: "POST",
      headers: { "Content-Type": "application/json", "Authorization": "Bearer " + getToken() },
      body: JSON.stringify({ subdivision: subValue }),
    })
      .then(async (r) => {
        const d = await r.json();
        if (!r.ok) throw new Error(d.error || "Could not follow");
        return d;
      })
      .then(() => { setBusy(false); loadFollows(); })   // refresh after change
      .catch((e) => { setError(e.message); setBusy(false); });
  }

  function unfollow(subValue) {
    setBusy(true);
    setError(null);
    fetch("http://localhost:8080/api/settings/unfollow", {
      method: "POST",
      headers: { "Content-Type": "application/json", "Authorization": "Bearer " + getToken() },
      body: JSON.stringify({ subdivision: subValue }),
    })
      .then(async (r) => {
        const d = await r.json();
        if (!r.ok) throw new Error(d.error || "Could not unfollow");
        return d;
      })
      .then(() => { setBusy(false); loadFollows(); })
      .catch((e) => { setError(e.message); setBusy(false); });
  }

  // Helper: turn an enum value like "DATA_ANALYST" into "Data Analyst"
  function labelFor(subValue) {
    for (const divKey of Object.keys(TAXONOMY)) {
      const found = TAXONOMY[divKey].subdivisions.find((s) => s.value === subValue);
      if (found) return found.label;
    }
    return subValue;
  }

  if (!data && !error) {
    return <div className="mx-auto max-w-2xl px-5 py-10 text-sm text-zinc-500">Loading settings...</div>;
  }

  return (
    <div className="mx-auto max-w-2xl px-5 py-10">
      {/* Header with back button */}
      <div className="flex items-center justify-between border-b border-white/10 pb-5 mb-8">
        <span className="text-xl font-bold tracking-tight">Settings</span>
        <button onClick={onBack} className="text-xs uppercase tracking-wider text-zinc-500 hover:text-zinc-200 transition-colors">
          Back to feed
        </button>
      </div>

      {error && <p className="mb-4 text-sm text-red-400">{error}</p>}

      {data && (
        <>
          {/* Home field */}
          <div className="mb-6">
            <div className="text-xs uppercase tracking-wider text-zinc-600 mb-2">Your field</div>
            <div className="rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm">
              {labelFor(data.homeSubdivision)}
              <span className="text-zinc-500"> · {TAXONOMY[data.homeDivision]?.label}</span>
              <span className="ml-2 text-xs text-zinc-600">(your home — can't be removed)</span>
            </div>
          </div>

          {/* Division usage indicator */}
          <div className="mb-6">
            <div className="text-xs uppercase tracking-wider text-zinc-600 mb-2">
              Divisions: {data.divisionsUsed} of {data.maxDivisions} used
            </div>
            <div className="flex gap-2">
              {data.activeDivisions.map((div) => (
                <span key={div} className="rounded-full border border-white/20 bg-white/10 px-3 py-1 text-xs">
                  {TAXONOMY[div]?.label || div}
                </span>
              ))}
            </div>
          </div>

          {/* Following list */}
          <div className="mb-6">
            <div className="text-xs uppercase tracking-wider text-zinc-600 mb-2">Following</div>
            {data.followedSubdivisions.length === 0 ? (
              <p className="text-sm text-zinc-600">You're not following any extra fields yet.</p>
            ) : (
              <div className="space-y-2">
                {data.followedSubdivisions.map((sub) => (
                  <div key={sub} className="flex items-center justify-between rounded-xl border border-white/10 bg-white/5 px-4 py-2.5">
                    <span className="text-sm">{labelFor(sub)}</span>
                    <button
                      onClick={() => unfollow(sub)}
                      disabled={busy}
                      className="text-xs text-red-400 hover:text-red-300 disabled:opacity-50"
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Add a subdivision, grouped by division */}
          <div>
            <div className="text-xs uppercase tracking-wider text-zinc-600 mb-2">Add a field to your feed</div>
            {Object.keys(TAXONOMY).map((divKey) => (
              <div key={divKey} className="mb-4">
                <div className="text-xs text-zinc-500 mb-1.5">{TAXONOMY[divKey].label}</div>
                <div className="flex flex-wrap gap-2">
                  {TAXONOMY[divKey].subdivisions.map((sub) => {
                    const alreadyFollowing = data.followedSubdivisions.includes(sub.value);
                    const isHome = data.homeSubdivision === sub.value;
                    return (
                      <button
                        key={sub.value}
                        onClick={() => follow(sub.value)}
                        disabled={busy || alreadyFollowing || isHome}
                        className="rounded-lg border border-white/10 bg-black/30 px-3 py-1.5 text-xs text-zinc-300 transition-colors hover:border-white/30 disabled:opacity-40 disabled:cursor-not-allowed"
                      >
                        {isHome ? sub.label + " (home)" : alreadyFollowing ? sub.label + " ✓" : "+ " + sub.label}
                      </button>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}

export default Settings;