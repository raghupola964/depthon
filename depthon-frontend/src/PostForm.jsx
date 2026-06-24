import { useState, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { getToken } from "./auth";

function PostForm({ onPostJudged }) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [verdict, setVerdict] = useState(null);
  const stompClientRef = useRef(null);

  function handleSubmit() {
    setSubmitting(true);
    setVerdict(null);

    fetch("http://localhost:8080/api/posts", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + getToken(),
      },
      body: JSON.stringify({ title, content }),
    })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || "Request failed");
        return data;
      })
      .then((data) => {
        // data.id = new post's ID, data.status = PENDING
        setVerdict(data);
        setSubmitting(false);
        setTitle("");
        setContent("");
        if (onPostJudged) onPostJudged();

        // Start listening for THIS post's verdict over WebSocket
        listenForVerdict(data.id);
      })
      .catch((err) => {
        setVerdict({ status: "BLOCKED", feedback: err.message });
        setSubmitting(false);
      });
  }

  function listenForVerdict(postId) {
    // Clean up any previous connection
    if (stompClientRef.current) {
      stompClientRef.current.deactivate();
    }

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      onConnect: () => {
        client.subscribe("/topic/verdicts", (message) => {
          const verdictData = JSON.parse(message.body);
          // Only react if it's the verdict for the post WE just submitted
          if (verdictData.postId === postId) {
            setVerdict({
              status: verdictData.status,
              feedback: verdictData.feedback,
            });
            // Refresh the feed so a newly-approved post appears right away
            if (onPostJudged) onPostJudged();
            client.deactivate(); // got our verdict, disconnect
          }
        });
      },
    });

    client.activate();
    stompClientRef.current = client;
  }

  const isPending = verdict?.status === "PENDING";
  const isApproved = verdict?.status === "APPROVED";
  const verdictColor = isApproved
    ? "border-green-500/40 bg-green-500/10 text-green-300"
    : isPending
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
        {submitting ? "Submitting..." : "Submit to the Gatekeeper"}
      </button>

      {verdict && (
        <div className={`mt-4 rounded-xl border px-4 py-3 animate-fadeUp ${verdictColor}`}>
          {isPending ? (
            <>
              <div className="text-xs font-bold tracking-wider mb-1">● UNDER REVIEW</div>
              <p className="text-sm leading-relaxed text-zinc-400">
                Submitted! The Gatekeeper is reviewing your post...
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