import { useState } from "react";
import { saveToken } from "./auth";
import { TAXONOMY } from "./taxonomy";

function Signup({ onSignedUp, onSwitchToLogin }) {
  const [fullName, setFullName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [division, setDivision] = useState("");        // chosen division
  const [subdivision, setSubdivision] = useState("");  // chosen subdivision
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // The subdivisions to show depend on the chosen division:
  const subdivisionOptions = division ? TAXONOMY[division].subdivisions : [];

  function handleSignup() {
    setSubmitting(true);
    setError(null);

    fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ fullName, username, email, password, subdivision }),
    })
.then(async (response) => {
        const text = await response.text();   // register returns plain text, not JSON
        if (!response.ok) {
          // On error, the body IS json ({"error":...}); try to parse it for the message
          try {
            const err = JSON.parse(text);
            throw new Error(err.error || "Sign up failed");
          } catch {
            throw new Error(text || "Sign up failed");
          }
        }
        return text;  // success: "User registered successfully: ..."
      })
      .then(() => {
        // Registration succeeded. Now log in automatically to get a token.
        return fetch("http://localhost:8080/api/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
        });
      })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error("Registered, but auto-login failed. Please log in.");
        saveToken(data.token);
        setSubmitting(false);
        if (onSignedUp) onSignedUp();
      })
      .catch((err) => {
        setError(err.message);
        setSubmitting(false);
      });
  }

  const inputClass = "w-full rounded-lg border border-white/10 bg-black/30 px-3 py-2.5 text-sm text-zinc-100 placeholder-zinc-600 outline-none focus:border-white/30 mb-2.5";

  return (
    <div className="mx-auto max-w-sm mt-16 rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-md">
      <h2 className="text-xl font-bold mb-4">Join Depthon</h2>

      <input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Full name" className={inputClass} />
      <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" className={inputClass} />
      <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" className={inputClass} />
      <input value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" type="password" className={inputClass} />

      {/* Division dropdown */}
      <select
        value={division}
        onChange={(e) => { setDivision(e.target.value); setSubdivision(""); }}
        className={inputClass}
      >
        <option value="">Choose your division...</option>
        {Object.keys(TAXONOMY).map((key) => (
          <option key={key} value={key}>{TAXONOMY[key].label}</option>
        ))}
      </select>

      {/* Subdivision dropdown - only enabled once a division is chosen */}
      <select
        value={subdivision}
        onChange={(e) => setSubdivision(e.target.value)}
        disabled={!division}
        className={inputClass}
      >
        <option value="">
          {division ? "Choose your role..." : "Pick a division first"}
        </option>
        {subdivisionOptions.map((sub) => (
          <option key={sub.value} value={sub.value}>{sub.label}</option>
        ))}
      </select>

      <button
        onClick={handleSignup}
        disabled={submitting || !subdivision || !fullName || !username || !email || !password}
        className="w-full rounded-lg bg-zinc-100 px-5 py-2.5 text-sm font-semibold text-black transition-all hover:bg-white active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed mt-1"
      >
        {submitting ? "Creating account..." : "Sign up"}
      </button>

      {error && <p className="mt-3 text-sm text-red-400">{error}</p>}

      <p className="mt-4 text-sm text-zinc-500">
        Already have an account?{" "}
        <button onClick={onSwitchToLogin} className="text-zinc-200 underline">
          Log in
        </button>
      </p>
    </div>
  );
}

export default Signup;