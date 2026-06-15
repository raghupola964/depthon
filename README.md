# Depthon

**An AI-moderated professional network where every post has to pass two filters before it shows up in a feed: quality and relevance.**

Most professional feeds are built around engagement, which tends to reward volume over substance. Depthon goes the other way. An AI gatekeeper scores each post for how much real insight it carries before it can be published, and a profession-based system keeps each person's feed focused on their own field. There's also a deliberate limit on how broad your feed can get, so it doesn't turn into endless scrolling.

---

## What makes it different

Every post goes through **two separate filters**:

| Filter | What it checks | How it works |
| --- | --- | --- |
| **Quality** | Is this post actually insightful enough to publish? | A Python AI service scores it. Low-quality posts get rejected with feedback explaining why. |
| **Relevance** | Does this post belong in the reader's professional world? | A two-level profession system (Divisions and Subdivisions) filters every feed. |

On top of that, there's a **community moderation layer**. If a post gets tagged to the wrong field, people in that field can report it. Once it gets enough reports, it's hidden from the feed but kept on the author's own profile.

---

## Architecture

Depthon runs as three separate services:

```
┌─────────────────────┐     REST      ┌──────────────────────┐
│  React Frontend      │ ────────────> │  Spring Boot Backend  │
│  (Vite + Tailwind)   │ <──────────── │  (Java 17, JWT auth)  │
│  localhost:5173      │     JSON      │  localhost:8080       │
└─────────────────────┘               └───────────┬──────────┘
                                                   │ REST (internal)
                                                   v
                                       ┌──────────────────────┐
                                       │  AI Gatekeeper        │
                                       │  (Python, FastAPI,    │
                                       │   LangGraph + Gemini) │
                                       │  localhost:8000       │
                                       └──────────────────────┘

       PostgreSQL 16 (Docker), stores users, posts, and reports
```

- **`depthon-core`** is the Spring Boot 3.3 backend (Java 17). It handles authentication with JWT, users, posts, the profession system, feeds, and community reports. It talks to PostgreSQL through Spring Data JPA.
- **`depthon-ai-gatekeeper`** is the Python service (FastAPI). It runs a LangGraph agent that judges post quality using Google's Gemini model and returns a verdict (approved, needs revision, or rejected) along with feedback.
- **`depthon-frontend`** is the React app (Vite + Tailwind). It has login and signup, the profession-filtered feed, a settings screen for managing the fields you follow, and a "my posts" view.

The backend calls the AI service to judge each post. If the AI service is down, posts are held as **pending** instead of being auto-approved, so quality control never gets silently skipped.

---

## Key features

**AI quality gate.** Every new post gets sent to the Python service, which scores how much real insight it has and sends back a structured verdict. Only substantial posts get published. The rest come back with specific feedback on what to improve.

**Profession system.** Professions are organized in two levels: broad **Divisions** (like Information Technology or Artificial Intelligence) that contain specific **Subdivisions** (like Software Developer or Data Scientist). These are modeled as type-safe enums, where each subdivision knows which division it belongs to.

**Field-based feeds.** When you sign up, you pick one subdivision as your "home" field. That's what your own posts get tagged with. Your feed shows posts from your home field plus any other fields you decide to follow.

**The 2-division cap.** You can follow extra fields, but only across **two divisions at most** at any one time. This is enforced on the backend with a look-ahead check, and it's the main anti-noise idea behind the whole project. The limit is intentional, not a missing feature.

**Community moderation.** Since posts are tagged by who wrote them rather than by what they're about, a post can end up in the wrong place. People in that field can report it, and once it hits a set number of distinct reports, it gets hidden from the feed but stays on the author's profile. A database-level unique constraint makes sure one person can only report a given post once.

---

## Tech stack

| Layer | Technologies |
| --- | --- |
| Frontend | React, Vite, Tailwind CSS |
| Backend | Java 17, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA |
| AI service | Python, FastAPI, LangGraph, Google Gemini |
| Database | PostgreSQL 16 (Dockerized) |
| Auth | JSON Web Tokens (jjwt) |
| Tooling | Maven, Docker Compose, Git |

---

## Running locally

**You'll need:** Java 17, Maven, Node.js, Python 3.11+, and Docker.

The project uses local secret files that aren't committed to the repo, so you'll need to add your own:
- `depthon-core/src/main/resources/application-secrets.properties` for the database password and a JWT secret
- `depthon-ai-gatekeeper/.env` for a `GOOGLE_API_KEY` (Gemini)

**1. Start PostgreSQL (Docker):**
```bash
cd depthon-core
docker compose up -d
```

**2. Start the AI gatekeeper (Python):**
```bash
cd depthon-ai-gatekeeper
python -m venv venv
# Windows: .\venv\Scripts\Activate.ps1   |   macOS/Linux: source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --port 8000 --reload
```

**3. Start the backend (Spring Boot):**
```bash
cd depthon-core
mvn spring-boot:run
```

**4. Start the frontend (React):**
```bash
cd depthon-frontend
npm install
npm run dev
```

Then open the frontend (default `http://localhost:5173`).

---

## Project status

This is a personal project I built to dig into full-stack development with an AI moderation piece on top. The core is implemented and works end to end: authentication, the AI quality gate, the profession system and filtered feeds, the follow system with the division cap, and community moderation.

A few things I'd like to add later: AI-based field classification at posting time, caching to speed up the feed, automated tests, and CI/CD.

---

## Author

**Raghu Pola**
- LinkedIn: [linkedin.com/in/raghu-pola](https://linkedin.com/in/raghu-pola)
- Email: raghupola964@gmail.com