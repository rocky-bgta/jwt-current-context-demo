# JWT + Current Context Demo — Simple, Packagewise

Java 17, Spring Boot 3, Maven, MongoDB (local), Redis (Docker).

## Project structure

```text
src/main/java/com/example/contextdemo/
  DemoApplication.java
  config/
    SecurityConfig.java
  security/
    JwtFilter.java
    JwtService.java
    ApiAuthenticationEntryPoint.java
  controller/
    AuthController.java
    RoomAController.java
    RoomBController.java
    RoomCController.java
    ApiErrorHandler.java
  service/
    AuthService.java
    RoomContextService.java
  model/
    RoomContext.java
  repository/
    RoomContextRepository.java
```

Each room has its own controller, so you can set breakpoints independently.

## Room rules

- **Room A**: valid JWT + Room A context ID
- **Room B**: valid JWT only
- **Room C**: valid JWT + Room C context ID
- Revoke A's context: A stops working; B and C still work.
- Revoke JWT (logout): A, B and C stop working with that JWT.

## Run on Windows

1. Start MongoDB on `localhost:27017`.
2. Start your existing Redis container: `docker start asat-redis`
   - Alternatively, use `docker compose up -d` to create Redis from this project.
3. Run `mvn clean spring-boot:run` in the project directory.
4. Import `postman/JWT-CurrentContext-Demo-Packagewise.postman_collection.json`.
5. Run requests in numerical order.

Demo login: `demo` / `demo123`.

## Postman flow

1. POST `/api/auth/login` with username and password. Copy JWT.
2. POST `/api/rooms/A/contexts` with Bearer JWT. Copy Room A context ID.
3. GET `/api/rooms/A` with Bearer JWT and `X-Current-Context` header.
4. POST `/api/rooms/C/contexts`. Copy separate Room C context ID.
5. GET `/api/rooms/C` with Bearer JWT and Room C context ID.
6. GET `/api/rooms/B` with Bearer JWT only.
7. DELETE `/api/rooms/A/contexts/{contextId}`.
8. GET `/api/rooms/A` with revoked context: HTTP 403.
9. GET `/api/rooms/C`: still works.
10. GET `/api/rooms/B`: still works.
11. POST `/api/auth/logout`.
12. GET `/api/rooms/B`: HTTP 401 (JWT revoked).

Postman automatically stores JWT, Room A context, and Room C context
in collection variables, but you can copy and paste them manually.

## Storage

- Redis: `jwt:active:<jwtId>` and `ctx:<contextId>` (2-hour TTL)
- MongoDB: database `current-context-demo`, collection `room_context`
- `RoomContextService.createContext` saves to MongoDB and Redis.
- `RoomContextService.checkContext` checks MongoDB and Redis.
- `RoomContextService.revokeContext` marks MongoDB context inactive and deletes Redis key.

## Important notes

This is a learning demo, not production authentication. Credentials are
hardcoded for clarity, and the default JWT signing secret is for local use.
Room A and C contexts are scoped to a room, user and JWT session.
JWT logout invalidates access even if a room context still exists.

**Build status:** Maven is not available in the creation environment, so
a successful compile/run and live Redis/MongoDB integration were not verified.
