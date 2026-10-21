# SoulM frontend

React 18 + TypeScript (strict) + Vite, React Router, TanStack Query, react-hook-form + Zod, Axios.

## Run it

```bash
npm install
cp .env.example .env.local   # then edit VITE_API_BASE_URL if the API is not on :8080
npm run dev                   # http://localhost:3000
```

Open the app at **http://localhost:3000** (not 127.0.0.1): the backend's CORS allows exactly that origin.

| Command | Purpose |
|---|---|
| `npm run dev` | Dev server on port 3000 (fixed on purpose) |
| `npm run build` | Type-check, then production build |
| `npm run typecheck` | `tsc --noEmit` |
| `npm run lint` | ESLint |
| `npm test` | Vitest |

## Environment variables

| Name | Required | Notes |
|---|---|---|
| `VITE_API_BASE_URL` | production only (dev defaults to `http://localhost:8080`) | No trailing slash. Public values only: `VITE_*` is bundled into the browser. |

## What exists

* Transport: `src/api/` (two Axios instances, `ApiError` normalisation for the backend's error shapes including per-field validation messages, single-flight and cross-tab token refresh, token store).
* Auth: `src/features/auth/` (login, signup and refresh API, session restore on page load, logout, cross-tab logout, `RequireAuth` and `PublicOnly` guards, role redirects that refuse open-redirect targets).
* Public pages: landing, login, signup (account, e-mail code, profile, photos), forgot and reset password, not found.
* Member area (`/app`, role USER), with its own layout:
  * **Discover:** nearby people one at a time, distance slider, browser-side filters, Like / Pass / View profile, match notice.
  * **Profile** (`/app/users/:id`): gallery, details, Like, Message, Report.
  * **Likes**, **Matches** (unmatch with confirmation) and **Messages** (live chat over STOMP with history, reconnection, delivery state).
* `/moderator` and `/manager` still show a **temporary** signed-in page.

## What is not built yet

My profile and profile editing, settings, notifications, Block, and the moderator and manager areas. The backend gaps behind several of them, and bugs that block likes and complaints, are listed in `docs/BACKEND_ANALYSIS.md` (Revision 3 first).

Sign-up leaves out Terms of Service and Privacy Policy consent on purpose (no text, nothing on the backend to store it); see Revision 2.

## Verification status

Not run against the backend. `npm install`, `tsc`, ESLint, Vitest and the dev server were not executed (the authoring sandbox had no network), so nothing is type-checked and no page has been seen in a browser. Dependency versions are caret ranges chosen from memory and may need adjusting on first install.

What was checked: all 107 TypeScript files parse. The modules with no third-party imports (dates, routing rules, photo rules, signup payload, discover filters, chat message state, formatting, JWT expiry) were compiled and their test cases executed in plain Node; all passed. The tests that need zod, axios or React (`schemas`, `authApi`, `errors`, `refreshCoordinator`, `likeFlow`) were written but not run.

Not verified at runtime and worth testing first: the WebSocket handshake at `/chat/websocket`, photo loading from `/uploads`, and every endpoint called by the member area.

First things to try once it installs: `npm run typecheck`, `npm test`, then sign up, log in, like someone, and open a conversation.
