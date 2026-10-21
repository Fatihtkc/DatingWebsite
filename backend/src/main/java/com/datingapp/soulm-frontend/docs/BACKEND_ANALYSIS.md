# SoulM backend analysis (phases 1 and 2)

Based on the files supplied so far. Markers: **✅** read directly from source, **🔶** inferred (from a service, config or naming), **⛔** not determinable from what was supplied.

---

## Revision 2: re-checked against the sources supplied after the first report

Sections 3 and 5 below were written before these files arrived; where they disagree, **this section wins**.

### Status of the earlier findings

| Finding | Status | Evidence |
|---|---|---|
| B1 auth endpoints need a login | **Fixed** | `SecurityConfig` now has `.requestMatchers("/api/auth/**").permitAll()` |
| B2 catch-all turns framework errors into 500 | **Partly fixed** | `MethodArgumentNotValidException` now returns 400 `VALIDATION_FAILED` with per-field messages; `HttpMessageNotReadable`, `MissingServletRequestParameter` and `MethodArgumentTypeMismatch` return 400. **Still falling through to 500:** `BadCredentialsException`, `ResponseStatusException`, `HandlerMethodValidationException`, `MaxUploadSizeExceededException`, `NoResourceFoundException` (see below) |
| B3 e-mail verification not enforced | **Probably fixed** | `verifyOtp` now stores an `EmailVerification` row (`verifiedAt`, `expiresAt = now + 15 min`). `UserServiceImpl.createUser` was not supplied, so the check at signup and its failure code are unconfirmed |
| B4 rate-limit paths, B5 `frontend-url`, B6 `/uploads` | Not re-supplied | Unverified |
| S1 to S10 | Not re-supplied | Unverified |

### Still open, and it affects the login page

`LoginServiceImpl` throws `BadCredentialsException` for a wrong password and `AuthenticationController` does not catch it, so `GlobalExceptionHandler#handleGenericException` answers **500** `INTERNAL_SERVER_ERROR`. The same happens on `/api/auth/refresh` with an expired or revoked token. Suggested handlers:

```java
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request){
    return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password.", request);
}

@ExceptionHandler(ResponseStatusException.class) // OtpSendRateLimiter throws a 429 this way
public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request){
    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
    return buildResponse(status, status.name(), ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), request);
}
```

Until then the UI cannot tell "wrong password" from "server error": it shows a generic failure message for a 500 and the wrong-password message only for a 401. Likewise a revoked refresh token answers 500, which the client treats as "could not find out", so the session is kept instead of being ended.

### New facts from the supplied sources

* **Login contract.** `POST /api/auth/login {email, password}` returns `JwtResponseDTO {id, username, token, refreshToken, role}`. The access token is called **`token`**. `POST /api/auth/refresh {refreshToken}` returns the same shape; `POST /api/auth/logout {refreshToken}` returns 204.
* **`username` is inconsistent.** Login returns the real username for users and the **e-mail** for staff; refresh returns the **e-mail for everyone** (`AuthTokens.username` is `cud.getUsername()`). The UI never uses it as a display name.
* **`UserCreateDTO`.** Required: `username` (3-30), `firstName` and `lastName` (2-50), `email` (valid, at most 254), `password` (12-128 with a lowercase letter, an uppercase letter and a digit, ASCII), `gender`, `latitude` (-90..90), `longitude` (-180..180). Optional: `birthDate` (`@Past` only, **nullable, no 18+ rule**), `height` (50-250), `location` (500), `shorterBio` (500), `hobbies` (1000), `favoriteMusic` (200), `weekendPlans` (500), `bodyType`, `relationshipType`, `smoke`, `alcohol`, `diet`, and `agePreference` and `distancePreference`, which are **free-text strings** (500) that nothing parses yet.
* **Enums** (all complete now): `Gender` MALE FEMALE OTHER; `BodyType` SLIM AVERAGE ATHLETIC HEAVY NO_PREFERENCE; `Diet` OMNIVORE VEGETARIAN VEGAN PESCATARIAN FLEXITARIAN OTHER NO_PREFERENCE; `RelationshipType` SERIOUS_RELATIONSHIP CASUAL_DATING FRIENDSHIP NO_PREFERENCE; `Alcohol` NEVER SOMETIMES REGULARLY NO_PREFERENCE; `Smoking` NON_SMOKER OCCASIONAL REGULAR TRYING_TO_QUIT NO_PREFERENCE (carried by the property `smoke`).
* **`UserUpdateDTO` changed:** `latitude` and `longitude` are now `@NotNull`, so every profile edit must resend coordinates. It also declares `username`, which `updateUser` ignores.
* **`User` entity defaults:** `approved=false`, `banned=false`, `confirmed=false`, `role=USER`. A new account can log in but stays out of Discover until a moderator approves it. `UserProfileDTO` has no `approved` flag, so the UI cannot show "awaiting review".
* **`UserMapper#toDTO(user)`** now filters to APPROVED images, which resolves the leak risk in S4 for that overload. `toAdminDTO(user)` does not filter.
* **`ErrorResponse`** gained a sixth component. The JSON name **`fieldErrors`** is inferred from the handler's variable name; the record itself was not supplied. If it differs, field messages fall back to the generic "One or more fields are invalid."
* **Signup now has a time window.** After `/verify` succeeds the server remembers the address for 15 minutes. The sign-up flow collects the profile after verification and creates the account in one request, so it re-verifies if more than 14 minutes pass.

### Decisions in the sign-up flow worth knowing

* **Terms of Service and Privacy Policy consent is not in the form.** There is no text to agree to and no field on the backend that records consent, so a checkbox would be meaningless. It needs real legal text plus a stored acceptance (version and timestamp).
* **Age 18+ is enforced in the browser only.** The backend accepts a null or any past `birthDate`. Add `@NotNull` and an 18+ validator to `UserCreateDTO`.
* **Coordinates are mandatory**, so sign-up needs browser geolocation. Denied permission blocks sign-up, with retry guidance. Coordinates are rounded to 3 decimals (about 110 m) before sending.
* **Photos come after the account exists.** Signup is JSON only; images go to `POST /api/users/me/images` with the new account's token. The step is optional.

---

## Revision 3: likes, matches, messaging, discovery and complaints

Sources now supplied: `LikeController`, `MatchController`, `ConversationController`, `MessageController`, `ComplaintController`, `StompAuthChannelInterceptor`, and the DTOs `NearbyUserDTO`, `ImageDTO`, `UserLikeDTO`, `MatchDTO`, `ConversationDTO`, `GetMessageDTO`, `SendMessageDTO`, `ComplaintCreateDTO`, `ComplaintDTO`, `LikeRequestDTO`.

### Bugs that stop a feature working (fix these first)

**R1. "People you liked" always answers 403.** `LikeController#listLikes` is mapped to `GET /api/likes/{userId}` and guarded by `@PreAuthorize("#userId == principal.id ...")`, but the method has no `userId` parameter, so `#userId` is null and the expression is never true. The page is wired to the endpoint and will work once this is added:

```java
public ResponseEntity<Page<UserLikeDTO>> listLikes(@PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails principal, @PageableDefault(page = 0, size = 20) Pageable pageable)
```

**R2. Filing a complaint cannot work as written.** `createComplaint` is mapped to `POST "/"` with `consumes = multipart/form-data` but reads the DTO with `@RequestBody`, which cannot read a multipart body (415, and the catch-all turns that into 500). The trailing slash also means `/api/complaints` does not match in Spring 6. The Report dialog sends the corrected shape (`POST /api/complaints`, JSON part `data`, file parts `images`), so apply:

```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ComplaintDTO> createComplaint(@RequestPart("data") @Valid ComplaintCreateDTO dto,
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestPart(value = "images", required = false) List<MultipartFile> files)
```

(The pasted `ComplaintController` also begins with a duplicated `package` line; check the file.)

### How likes and matches actually work (and what that forces on the client)

* **A like never creates a match.** `POST /api/likes/users/{likedId}/like` returns 201 and nothing else. A match exists only after `POST /api/matches?user2Id=`, which succeeds if both likes exist (idempotent, 200) and answers 409 otherwise. The client therefore likes, then asks for a match, and treats 409 as "not mutual yet". Whoever likes second is the one who triggers it; the first person finds the match under Matches (or via the NEW_MATCH notification, if `MatchCreatedEvent` sends one: that listener was not supplied).
* **The extra request is subject to `matches` rate limiting** (20 per minute per user), so very fast liking can leave a like without a match check. The UI says so and the match still appears once either side asks again. *Recommended: create the match inside `addLike` when the reverse like exists and return a `matched` flag in the response. One request, no race, no rate-limit interaction.*
* **Unmatch leaves both likes in place.** `deleteMatch` removes only the match row, so any later `POST /api/matches` for the same pair succeeds again. The client never asks for a match after a 409 "already liked" for that reason. *Recommended: delete both likes (or record the unmatch) when unmatching.* Likewise removing a like does not end an existing match.
* **`GET /api/matches` returns `MatchDTO {id, user, matchedAt}`**, where `user` is assumed to be the *other* person (`MatchMapper` not supplied).

### Messaging

* **`POST /api/conversations/{userId}`** returns a bare JSON number (the conversation id), creating the conversation if needed; 400 `INVALID_REQUEST` (generic text) when the two are not matched.
* **No conversation list.** `ConversationDTO` carries `lastMessageAt`, `user1HasUnread` and `user2HasUnread`, but nothing returns it and nothing marks messages read (`GetMessageDTO.isRead` is never updated). The Messages page lists your **matches** instead; there are no previews or unread badges. *Recommended: `GET /api/conversations` returning the other person, last message and unread flag, plus a mark-read call.*
* **`SendMessageDTO`:** `conversationId`, `content` (not blank, at most 2000) and `isImage` are all required. There is **no endpoint that creates an image message**, so the UI cannot send photos; it displays `isImage` messages by loading `content` as an image URL (unverified, nothing produces them yet).
* **STOMP, now verified.** CONNECT needs `Authorization: Bearer <jwt>` (checked once, so the client fetches a fresh token before every connect). SUBSCRIBE is allowed only to `/topic/conversations/{id}` and only for members (this closes S7 for subscriptions). SEND must go to `/app/sendMessage`, 20 per 10 s per user.
* **Failure feedback is partial.** Interceptor failures (bad token, wrong destination, rate limit) come back as STOMP ERROR frames. Failures inside `receiveMessage` (banned, not matched, blocked, `@Valid`) are logged and never reported to the sender. The UI therefore shows a message as "Sending" until the server's copy arrives and marks it "Not delivered" after 10 seconds. *Recommended: `@MessageExceptionHandler` with `@SendToUser("/queue/errors")`, plus a client id in `SendMessageDTO` for exact matching.*
* **Message order and paging.** History is newest first. New messages shift page boundaries, so adjacent pages can overlap; the client merges by id.
* **Not verified at runtime:** the client connects to the raw WebSocket that SockJS registers at `/chat/websocket` (so `sockjs-client` is not needed). If the handshake fails, that is the first thing to check.

### Discovery

* **`NearbyUserDTO` gained** `height`, `location`, `hobbies`, `favoriteMusic`, `weekendPlans` and `images`, but the `toNearbyDTO` supplied earlier does not set them. The UI uses embedded images when present and otherwise loads `GET /api/users/{id}/images` for the one person on screen.
* **Still missing server-side:** age, gender and relationship filters (the UI filters the people already loaded, which is why filters say "so far"), exclusion of people already liked, and any notion of "pass". Passing is remembered for the browser session only. *Recommended: `NOT EXISTS` on likes in the nearby query, optional filter parameters, and a `passes` table if passes should persist.*
* `LikeRequestDTO` is unused by any controller.

### Other

* **No block endpoint was supplied**, so there is no Block action yet; Report is available on profiles and in chat.
* **`RateLimitConfig` was not re-supplied.** Last seen, its `likes` rule matched `/api/likes` while the real path is `/api/likes/users/{id}/like`, and its `complaint` rule matched `/api/complaints` while the controller path is `/api/complaints/`. If unchanged, neither limit ever applies.
* **`/uploads/**` access (B6) is still unconfirmed.** Photos load with a normal `<img>`; if the browser is refused, the client retries through an authenticated request and then falls back to a letter avatar.
* Complaint endpoints: list and detail allow MODERATOR and MANAGER; delete is MODERATOR only. `ComplaintDTO` is `{id, complainant, complained, reason, complaintDate, images}` with both parties as `UserAdminDTO`.

---

## 1. Architecture summary

| Area | What the source shows |
|---|---|
| Stack | Spring Boot 3.4.4, Java 17, MySQL with spatial types, JPA `ddl-auto=update`, Lombok, MapStruct-style mappers 🔶 |
| Accounts | Three separate tables/entities: `User`, `Moderator`, `Manager`. They share the `Role` enum but **numeric IDs are only unique within a table**. `CustomUserDetailsServiceImpl` resolves by e-mail in the order users, moderators, managers. |
| Auth | Stateless. HS512 JWT, 15 min, **subject = e-mail, no role or id claim**. Rotating refresh token, 30 days, SHA-256 hashed, family revocation on re-use. Login, signup and refresh all return tokens **in the JSON body**. BCrypt. |
| Authorisation | `@EnableMethodSecurity` with `@PreAuthorize`. Role authority is `ROLE_<name>`. |
| CORS | One origin (`http://localhost:3000`), methods GET/POST/PUT/DELETE/OPTIONS (**no PATCH**), headers `Authorization` and `Content-Type` only, `allowCredentials=false`. Cookie auth is therefore not possible without a change. |
| Rate limiting | Bucket4j + Caffeine, in memory per instance, first matching rule wins, keyed by `getRemoteAddr()`. Returns `429 {"error": "..."}` (different shape from every other error). |
| Real time | STOMP. Endpoint `/chat` (SockJS), simple broker `/topic`, application prefix `/app`, 16 KB message limit. Auth happens in `StompAuthChannelInterceptor` ⛔ (not supplied). |
| Files | Local disk (`file.upload-dir`), JPG/PNG only, ≤5 MB each, ≤10 files and ≤25 MB per batch, re-encoded to ≤1600 px with a random UUID name. AWS Rekognition screens every image. |
| Push | Firebase Cloud Messaging via an application event. No WebSocket channel for notifications. |
| Pagination | Spring `Page<T>`; default size 20, max 50 (`spring.data.web.pageable.max-page-size`). |
| Errors | `ErrorResponse {timestamp, status, code, message, path}` from `GlobalExceptionHandler`, `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler`. Plus the rate-limit shape above. |

---

## 2. Endpoint inventory

### A. Verified from controller source

| Method and path | Access | Request | Response |
|---|---|---|---|
| `GET /api/users/profile` | authenticated | – | `UserProfileDTO` ⚠ looks up the **users** table by principal id, so it is wrong for staff |
| `GET /api/users/allprofiles` | MANAGER, MODERATOR | `page,size` | `Page<UserProfileDTO>` |
| `GET /api/users/profiles` | authenticated | `page,size` | `Page<UserProfileDTO>` (approved and not banned; **no filters, does not exclude self, blocked or already-liked**) |
| `GET /api/users/{id}` | authenticated | – | `UserProfileDTO`; 404 if blocked either way |
| `GET /api/users/by-email/{email}` | authenticated | – | `UserProfileDTO` ⚠ account enumeration; the UI will not use it |
| `GET /api/users/check-user` | public | `email,username` | 200, or 409 `DUPLICATE_RESOURCE` ("Email is already in use" / "Username is already in use") |
| `POST /api/users/signup` | public | `UserCreateDTO` | **200** `JwtResponseDTO` (auto-login) |
| `PUT /api/users/profile/{id}` | `#id == principal.id` | `UserUpdateDTO` (full overwrite) | `UserProfileDTO` |
| `DELETE /api/users/{id}` | `#id == principal.id` or MODERATOR | – | 204 (hard delete) |
| `GET /api/users/nearby` | authenticated | `lat, lon, distanceKm` (default 50, 0.1 to 100), `page,size` | `Page<NearbyUserDTO>` (no images) |
| `GET /api/users/me/images` | authenticated | – | `List<ImageDTO>` (**approved only**) |
| `GET /api/users/{userId}/images` | authenticated | – | `List<ImageDTO>` (approved only; **no block check**) |
| `POST /api/users/me/images` | USER | multipart, repeated part `images` | 201 `UploadedImagesResponseDTO {images, rejected}`. **Replaces all existing images.** |
| `GET /api/admin/moderators` | MANAGER | `page,size` | `Page<ModeratorUpdateDTO>` |
| `GET /api/admin/moderator/{id}` | MANAGER, MODERATOR | – | `ModeratorUpdateDTO` |
| `POST /api/admin/moderators` | MANAGER | multipart: part `data` (**JSON, must carry `Content-Type: application/json`**), optional part `image` | 201 `ModeratorUpdateDTO` |
| `PUT /api/admin/moderators/{id}` | MANAGER | multipart as above | `ModeratorUpdateDTO` |
| `DELETE /api/admin/moderators/{id}` | MANAGER | – | 204 (hard delete) |
| `PUT /api/admin/moderator/change-password` | MANAGER, MODERATOR | `PasswordChangeRequestDTO` | 200 plain text |
| `GET /api/admin/managers` | MANAGER | `page,size` | `Page<ManagerUpdateDTO>` |
| `GET /api/admin/manager/{id}` | MANAGER | – | `ManagerUpdateDTO` |
| `POST /api/admin/managers` | MANAGER | multipart as above | 201 `ManagerUpdateDTO` |
| `PUT /api/admin/managers/{id}` | MANAGER | multipart as above | `ManagerUpdateDTO` |
| `DELETE /api/admin/managers/{id}` | MANAGER, not self | – | 204 |
| `PUT /api/admin/manager/change-password` | MANAGER, MODERATOR | `PasswordChangeRequestDTO` | **204** (inconsistent with the moderator twin) |
| `GET /api/admin/users/search` | MANAGER, MODERATOR | `name, page,size` | `Page<UserAdminDTO>` |
| `PUT /api/admin/users/{id}` | MANAGER, MODERATOR | `UserAdminDTO` | `UserAdminDTO` |
| `GET /api/stats` | **MODERATOR only** | – | `UserStatsDTO` |
| `POST /api/auth/send-verification` | ⚠ see B1 | query `email` | 200 text |
| `POST /api/auth/verify` | ⚠ see B1 | query `email, otp` | 200 text |
| `POST /api/auth/forgot-password` | ⚠ see B1 | `{email}` | 200 text |
| `GET /api/auth/validate` | ⚠ see B1 | query `token, type` (`PASSWORD_RESET`) | 200 text |
| `POST /api/auth/reset-password` | ⚠ see B1 | `{token, newPassword}` | 200 text |
| STOMP `SEND /app/sendMessage` | authenticated | `SendMessageDTO {conversationId, content, isImage}` | broadcast of `GetMessageDTO` to `/topic/conversations/{conversationId}` |

### B. Path known from configuration only (contract ⛔)

`POST /api/auth/login`, `POST /api/auth/refresh`, `POST /api/auth/logout`, `/api/token/**` (purpose unknown), `POST /api/likes`, `POST /api/matches`, `POST /api/complaints`, `/chat/**` (SockJS handshake).

### C. Service exists, controller not supplied (path and DTOs ⛔)

Likes: list sent (`Page<UserLikeDTO>`), add, remove **by like id**. Matches: list, create, delete. Conversations: get or create. Messages: history (`Page<GetMessageDTO>`, newest first). Notifications: list, unread count, mark all read, update settings, register and clear FCM token. Complaints: list, get, create (multipart evidence), delete. Moderation: approve user, ban user, pending users, complaints. Pending images: list and approve. Blocks: block, unblock, list. User password change: `changePassword(id, newPlainPassword)`.

---

## 3. DTO and enum reference

### Enums

| Enum | Values | Status |
|---|---|---|
| `Role` | `USER`, `MODERATOR`, `MANAGER` | ✅ |
| `NotificationType` | `NEW_MATCH`, `NEW_MESSAGE`, `NEW_LIKE` | ✅ (exhaustive switch) |
| `ModerationStatus` | `PENDING`, `APPROVED`, `REJECTED` | ✅ |
| `Token.TokenType` | at least `PASSWORD_RESET` | 🔶 |
| `OtpPurpose` | at least `EMAIL_VERIFICATION` | 🔶 |
| `Gender`, `BodyType`, `RelationshipType`, `Diet`, smoke and alcohol types | – | ⛔ (type of `smoke` and `alcohol` unknown) |

### DTO fields recoverable from service code

JSON property names assume default Jackson naming; DTO sources were not supplied, so **every one of these needs confirming**.

| DTO | Fields seen |
|---|---|
| `LoginRequestDTO` | `email`, `password`. **Login is by e-mail only, not username.** |
| `JwtResponseDTO` | constructed as `(id, username, jwt, refreshToken, role)`; JSON names ⛔. Staff get their **e-mail** as `username`. |
| `UserCreateDTO` | `latitude`, `longitude`, plus whatever `UserMapper.toEntity` reads (`email`, `username`, `password` at least) |
| `UserUpdateDTO` | `firstName, lastName, birthDate, gender, height, bodyType, location, latitude?, longitude?, relationshipType, agePreference, distancePreference, smoke, alcohol, shorterBio, diet, hobbies, favoriteMusic, weekendPlans`. **`updateUser` overwrites every field, so omitted fields become null.** Send the full object, prefilled from the profile. |
| `UserAdminDTO` | `approved, banned, confirmed, personalityScore` (+ read-only profile fields) |
| `NearbyUserProjection` | `id, username, firstName, lastName, birthDate, gender, bodyType, relationshipType, smoke, alcohol, diet, shorterBio, distance` (km). No images, no coordinates. |
| `PasswordChangeRequestDTO` | `oldPassword`, `newPassword` |
| `ForgotPasswordRequest` / `PasswordResetRequest` | `email` / `token`, `newPassword` |
| `SendMessageDTO` | `conversationId`, `content`, `isImage` (nullable Boolean). No client message id. |
| `NotificationSettingsDTO` | `matchNotificationsEnabled`, `messageNotificationsEnabled`, `likeNotificationsEnabled` |
| `ComplaintCreateDTO` | `complainedId`, `reason` (+ multipart evidence files) |
| `ComplaintDTO` (from projection) | `id, reason, complaintDate`, complainant and complained `{Id, Username, FirstName, LastName, Email, ImageUrl}`, `complaintImageUrl` (**first image only**) |
| `UploadedImagesResponseDTO` | `images: ImageDTO[]`, `rejected: {file name, message}[]` (names ⛔) |
| `UserStatsDTO` | total users, average age, gender distribution, relationship distribution (names ⛔) |

### Conventions

* **Image URLs.** User and complaint images are stored as **relative** `/uploads/<uuid>.<jpg|png>`; staff images are stored as **absolute** URLs built from the request. The UI resolves both.
* **Dates.** `LocalDateTime` (no zone) for timestamps; treat as server-local time.
* **OTP.** 6 digits, 10 minutes, 5 attempts. Send cooldown 60 s per e-mail; 3 per 10 min and 10 per 24 h per e-mail; 30 per minute globally; IP rules add 5 per 10 min each on send and verify.
* **Password reset token.** UUID in an e-mailed link `{app.frontend-url}/reset-password?token=…`, 15 minutes, single use; 3 requests per 15 min per e-mail.
* **Login lockout.** 5 failures per e-mail per 15 min (429 `TOO_MANY_LOGIN_ATTEMPTS`), 15 per IP, plus the 5 per minute per IP rate-limit rule.

---

## 4. Role and permission matrix

| Capability | USER | MODERATOR | MANAGER |
|---|:-:|:-:|:-:|
| Discover (`nearby`), view profiles and images | ✅ | ✅ (not meaningful) | ✅ (not meaningful) |
| Upload own images | ✅ | – | – |
| Update own user profile | ✅ | ⚠ only if a user row shares the id | ⚠ same |
| Delete user | self | **any user** | ⚠ only if ids coincide |
| List all users | – | ✅ | ✅ |
| Search users, edit approved/banned/confirmed/personality | – | ✅ | ✅ |
| Approve or ban (ModerationController) | ⛔ | ⛔ | ⛔ |
| Complaints list, detail, delete | ⛔ | ⛔ | ⛔ |
| Statistics | – | ✅ | **–** |
| List moderators / managers | – | – | ✅ |
| Read one moderator | – | ✅ (**any**) | ✅ |
| Read one manager | – | – | ✅ |
| Hire, update, delete moderator | – | – | ✅ |
| Hire, update, delete manager | – | – | ✅ (delete not self) |
| Change own password (staff) | – | ✅ | ✅ |

---

## 5. Findings

### B. Blockers (will break the UI as written)

* **B1. Five `/api/auth/*` endpoints require a login.** `SecurityConfig.permitAll()` lists login, signup, check-user, refresh, logout and `/api/token/**` only. `send-verification`, `verify`, `forgot-password`, `validate` and `reset-password` fall under `anyRequest().authenticated()`, so signup OTP and password recovery return 401 for anonymous callers. *Fix: add them to `permitAll()`.*
* **B2. The catch-all handler turns framework errors into 500.** `@ExceptionHandler(Exception.class)` also catches `MethodArgumentNotValidException` (every `@Valid` failure), `HandlerMethodValidationException` (the `@DecimalMin/@DecimalMax` on `/nearby`), `HttpMessageNotReadableException`, `MissingServletRequestParameterException`, `MaxUploadSizeExceededException`, `NoResourceFoundException`, `BadCredentialsException` (wrong password, failed refresh) and `ResponseStatusException` (**the OTP send limiter's 429 becomes 500**). Unless `AuthController` handles `BadCredentialsException` itself, a wrong password is a 500. Field-level validation messages never reach the client. *Fix: add handlers returning 400 with per-field details, 401, 413, 404, and rethrow `ResponseStatusException`'s status.*
* **B3. Email verification is not enforced.** `/verify` only checks the code and discards it; nothing records that the address was verified, and `/api/users/signup` does not ask for proof. The OTP step can be skipped by calling signup directly. *Fix: issue a short-lived signed "email verified" token from `/verify` and require it at signup.*
* **B4. Rate-limit rules point at paths that do not exist.** `RateLimitConfig` matches `/api/auth/signup` and `/api/auth/check-user`; the real routes are `/api/users/signup` and `/api/users/check-user`, so those two limits never apply.
* **B5. `app.frontend-url=https://SoulM`** is not a real address; reset e-mails will link there. Use `http://localhost:3000` in dev.
* **B6. `/uploads/**` authentication is unknown.** It is not in `permitAll()`. A browser `<img>` cannot send an `Authorization` header, so if no other rule applies every photo would 401. ⛔ needs the file-serving controller or `WebMvcConfigurer`. Options: permit `GET /uploads/**` (UUID URLs, but unapproved images become reachable by URL), or have the UI fetch images as blobs with the bearer token.

### S. Security

* **S1. Cross-table ID collisions.** `#id == principal.id` in `PUT /api/users/profile/{id}` and `DELETE /api/users/{id}` has no role check, so a moderator or manager whose id equals a user's id passes it. *Fix: `hasRole('USER') and #id == principal.id`.*
* **S2. Staff password endpoints are not role-scoped.** `PUT /api/admin/manager/change-password` also admits MODERATOR and looks up the **manager with the moderator's id**, which turns a moderator into an online guesser of a manager's password (no rate-limit rule). The reverse holds too. *Fix: scope each endpoint to its own role.* Neither revokes refresh tokens after a change.
* **S3. `likerId==likedId` compares boxed `Long` references** (`LikeServiceImpl.addLike`). For ids above 127 the self-like guard never triggers. *Fix: `.equals`.*
* **S4. Profile and image reads are over-permissive.** `GET /api/users/{id}` ignores approved/banned state; `GET /{userId}/images` has no block check; `by-email` allows account lookup. Whether `UserMapper.toDTO(user)` (called without the approved-image list) leaks non-approved images ⛔.
* **S5. Banned users can still log in.** `login()` does not check `banned`; every later call is then a 401 and refresh fails with "Account is locked" (a 500, see B2). The UI cannot tell "banned" from "expired". *Fix: reject at login with 403 `ACCOUNT_BANNED`.*
* **S6. Moderators can read any moderator** (`GET /moderator/{id}`) and can hard-delete any user.
* **S7. STOMP.** Anyone may attempt to subscribe to `/topic/conversations/{id}`. Whether the interceptor authorises SUBSCRIBE ⛔. Errors thrown in `@MessageMapping` have no `@MessageExceptionHandler`, so a rejected send (blocked, unmatched, banned) is silent.
* **S8. Rate limiting keys on `getRemoteAddr()`.** Behind a reverse proxy all users share one bucket unless forwarded headers are configured.
* **S9. OTP and e-mail travel in the query string** (`/send-verification`, `/verify`), so they land in access logs. The UI follows the contract; moving them to a JSON body is recommended.
* **S10. Forgot-password sends an e-mail even when no account exists**, and only for the `users` table (staff cannot recover passwords).

### C. Missing capabilities (the spec asks for these; the backend does not offer them)

| Spec item | Gap | Smallest backend change |
|---|---|---|
| Who am I / session restore | No `/me` for any role; JWT has no role claim; `GET /users/profile` is wrong for staff | Rely on login/refresh response for `{id, role}`; add `GET /api/auth/me` returning id, role, display name |
| Discover filters (age, relationship type, gender) | `/nearby` filters by distance only; `/profiles` has no filters | Add optional params to the native query |
| Discover cards with photos | `NearbyUserDTO` has no images | Join the first approved image into the projection |
| Discover excludes already-liked/matched | Not excluded | `NOT EXISTS` on likes in the query |
| Likes received / "who liked me" | Not available | Not requested by the spec; fine |
| Match creation | `addLike` neither creates a match nor sends NEW_MATCH; `createMatch` is a separate call that 409s unless both likes exist. ⛔ who triggers it | Show `LikeController` and `MatchController` |
| Conversation list with preview, unread | No list-by-user query; messages have `isRead` but no mark-read operation | `GET /api/conversations` (last message, unread count) and `PUT /api/conversations/{id}/read` |
| Message error feedback | Silent failures over STOMP | `@MessageExceptionHandler` + `@SendToUser("/queue/errors")` |
| Image management | Upload replaces **all** images; no single delete; PENDING images are invisible even to their owner (they appear only after a Rekognition outage and moderator approval) | Add/delete single image; return own PENDING images with status |
| Reject image (moderator) | Service has `approveImage` only | Add `rejectImage` (set REJECTED, delete file) |
| Complaint category, status, notes, resolve | `Complaint` has `reason` and a date only; the only operation is hard delete | Add `status`, `resolutionNote`; replace delete with resolve/dismiss |
| Complaint search, filter, sort | Fixed `ORDER BY complaint_date DESC`; native query | Add optional `status` and `q` params |
| Complaint evidence | Projection returns only the first image | Return all images in the detail endpoint |
| Notification settings (read) | No getter; `updateSettings` 404s until a settings row exists (created only when an FCM token is first stored) | Create a row at signup; add `GET` |
| Mark one notification read | Only `markAllAsRead` | Optional |
| Change e-mail | No service or endpoint | New OTP purpose + endpoint; JWT subject is the e-mail, so re-issue tokens |
| Change password (users) | Service exists, no supplied endpoint; no old-password check | Endpoint requiring old password |
| Edit own staff profile (moderator) | `PUT /moderators/{id}` is MANAGER only | Allow `#id == principal.id` for MODERATOR |
| Manager dashboard | `/api/stats` is MODERATOR only and holds four numbers; no registrations over time, approved/pending/banned counts, matches, messages, complaints | Allow MANAGER; add the missing aggregates and date params |
| Minimum age 18 | Not visible (DTO validation ⛔) | Bean-validate `birthDate` |
| Terms of Service, Privacy Policy | No content or endpoint | Owner supplies text; the UI will host static pages |
| Staff invitation flow | Hiring sets a password supplied by the manager | Optional |

### Notes for implementation (verified quirks)

* `data` multipart parts must be sent as a `Blob` with `type: 'application/json'`, otherwise Spring answers 415.
* Do **not** set `Content-Type` manually on multipart requests; the browser adds the boundary.
* `POST /api/users/signup` returns **200**, not 201.
* After `POST /api/users/me/images`, read `rejected` for per-file reasons; the response still counts as success if at least one file was accepted.
* The upload semaphore allows 4 concurrent uploads; a fifth gets 429 `TOO_MANY_REQUESTS`.
* Refresh tokens rotate and re-use revokes the family: refreshes must be single-flight within a tab and serialised across tabs (implemented in `src/api/refreshCoordinator.ts`).
* Raw WebSocket endpoint for the SockJS registration is `/chat/websocket`, so `@stomp/stompjs` can connect without `sockjs-client`. STOMP CONNECT auth header ⛔.

---

## 6. Files needed to unblock the rest

1. `AuthController` plus `JwtResponseDTO`, `LoginRequestDTO`, `RefreshTokenService` (the `AuthTokens` record) and any logout/refresh request DTOs. **Unblocks login, session restore, guards, role redirects.**
2. `StompAuthChannelInterceptor`. **Unblocks chat.**
3. Every DTO under `dto/**` with validation annotations, plus `ImageDTO`, `UploadedImagesResponseDTO`, `BatchUploadResult`, `PasswordChangeRequestDTO`, `ForgotPasswordRequest`, `PasswordResetRequest`.
4. Every enum under `enums/**`.
5. Controllers: likes, matches, conversations, messages, notifications, complaints, moderation, blocks, images moderation, anything serving `/uploads`.
6. Entities `User`, `Moderator`, `Manager` (field defaults such as `approved`), and `UserMapper`.

---

## 7. Proposed frontend architecture

```
src/
  api/          axios instances, error normalisation, refresh coordinator, one module per backend area
  app/          router, providers (React Query, router)
  components/   common (Button, FormField, Alert, ...), public, and later profile/dating/chat/admin
  config/       env
  features/     auth, discover, likes, matches, messages, notifications, moderation, management  (hooks + feature components)
  hooks/ layouts/ pages/{public,auth,user,moderator,manager}/ styles/ types/ utils/
```

* **Server state:** TanStack Query. Mutations are never retried. Each mutation invalidates the keys it affects.
* **Forms:** react-hook-form with Zod; server field errors are shown once the backend returns them (see B2).
* **HTTP:** `http` (bearer token, one transparent refresh on 401) and `publicHttp` (no token, no refresh). Everything rejects with `ApiError`.
* **Auth:** a session object `{userId, displayName, role}` owned by the frontend, mapped from the login/refresh response in a single adapter. Access token in memory, refresh token in `localStorage` (see `tokenStore.ts` for the trade-off). Route guards check `role` but the backend remains the authority.
* **Chat:** one `@stomp/stompjs` client per session (never per render), `beforeConnect` refreshes the access token, subscription per open conversation, de-duplication by message id, reconnect with back-off, connection banner.
* **Styling:** hand-written CSS with tokens; light theme; reduced-motion respected.
* **Tests:** Vitest + Testing Library + MSW at the network boundary.

## 8. Implementation plan (by dependency)

| # | Step | Needs | State |
|---|---|---|---|
| 0 | Scaffold, design system, transport layer, landing, forgot and reset password | – | **Written, not yet executed** (see README) |
| 1 | Auth: login, session restore, logout, guards, role redirect | – | **Written, not yet executed** |
| 2 | Signup: account, OTP, profile setup, images | `createUser` source to confirm the unverified-email error | **Written, not yet executed** |
| 3 | Discover, profile view, likes, matches | R1 for the likes list | **Written, not yet executed** |
| 4 | Conversations | previews and unread need backend work (Revision 3) | **Written, not yet executed** |
| 5 | My profile, edit profile, settings | items 3, 5 | Blocked |
| 6 | Moderator area | items 3, 5 | Blocked |
| 7 | Manager area | items 3, 5 | Blocked |
| 8 | Polish, accessibility and responsive pass, end-to-end checks | all | – |
