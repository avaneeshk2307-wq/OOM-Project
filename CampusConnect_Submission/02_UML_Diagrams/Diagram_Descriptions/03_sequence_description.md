# Diagram 3: Sequence Diagram Description

## 1. Diagram Title
**CampusConnect Authentication & JWT Session Sequence Diagram**

## 2. Diagram Purpose & Academic Importance
The Sequence Diagram models dynamic behavioral interaction across time. It traces the exact chronological exchange of messages between actors, presentation components, Spring REST controllers, security filter managers, user repositories, and database engines during user authentication.

## 3. Detailed Chronological Flow
1. User enters username and password and selects role on `LoginPage.tsx`.
2. Frontend issues HTTP POST request to `/api/v1/auth/login`.
3. `AuthController` intercepts request and constructs `UsernamePasswordAuthenticationToken`.
4. Spring Security's `AuthenticationManager` delegates to `UserDetailsServiceImpl`.
5. `UserDetailsServiceImpl` queries `UserAccountRepository` $ightarrow$ SQL `SELECT` executed on `user_accounts`.
6. Database returns persisted `UserAccount` record including salt and BCrypt password hash.
7. `PasswordEncoder` evaluates `matches(raw, hashed)`.
8. Upon successful validation, `JwtTokenProvider` signs an HMAC-SHA256 JWT containing claims (`username`, `role`, `personId`).
9. `AuthController` returns HTTP 200 with `LoginResponse` DTO.
10. Frontend `AuthContext` writes token and serialized user object to `localStorage`.
11. Client-side router evaluates `user.role` and redirects to `/admin`, `/faculty`, or `/student`.

## 4. Source Code Mapping
- Presentation: `frontend/src/pages/auth/LoginPage.tsx`, `frontend/src/context/AuthContext.tsx`
- Controller: `backend/src/main/java/com/campusconnect/controller/AuthController.java`
- Security Engine: `com.campusconnect.security.JwtTokenProvider`, `UserDetailsServiceImpl`
- Persistence: `com.campusconnect.repository.UserAccountRepository`
