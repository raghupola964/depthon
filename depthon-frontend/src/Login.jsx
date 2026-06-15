import { useState } from "react";
import { saveToken } from "./auth";

function Login({ onLoggedIn, onSwitchToSignup }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  function handleLogin() {
    setSubmitting(true);
    setError(null);

    fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || "Login failed");
        return data;
      })
      .then((data) => {
        saveToken(data.token);     // put the token in the drawer
        setSubmitting(false);
        if (onLoggedIn) onLoggedIn();   // tell App we're logged in
      })
      .catch((err) => {
        setError(err.message);
        setSubmitting(false);
      });
  }

  return (
    <div className="mx-auto max-w-sm mt-20 rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md">
      <h2 className="text-xl font-bold mb-4">Log in to Depthon</h2>

      <input
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        className="w-full rounded-lg border border-white/10 bg-black/30 px-3 py-2.5 text-sm text-zinc-100 placeholder-zinc-600 outline-none focus:border-white/30 mb-2.5"
      />
      <input
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        type="password"
        className="w-full rounded-lg border border-white/10 bg-black/30 px-3 py-2.5 text-sm text-zinc-100 placeholder-zinc-600 outline-none focus:border-white/30 mb-3"
      />

      <button
        onClick={handleLogin}
        disabled={submitting}
        className="w-full rounded-lg bg-zinc-100 px-5 py-2.5 text-sm font-semibold text-black transition-all hover:bg-white active:scale-95 disabled:opacity-50"
      >
        {submitting ? "Logging in..." : "Log in"}
      </button>

      {error && <p className="mt-3 text-sm text-red-400">{error}</p>}

      <p className="mt-4 text-sm text-zinc-500">
        No account?{" "}
        <button onClick={onSwitchToSignup} className="text-zinc-200 underline">
          Sign up
        </button>
      </p>
    </div>
  );
}

export default Login;