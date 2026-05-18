# Technical documentation

This document describes the e-commerce microservices platform under `Spring-application-main`: architecture, stack, security, data, and how the pieces interact at runtime.

## Purpose and scope

The system is a portfolio-oriented **e-commerce** platform demonstrating microservices, an API gateway, JWT auth, Redis-backed rate limiting, role-based APIs, and an **Angular** single-page application. It is not a full production SaaS (no Kubernetes, message buses, or distributed tracing in the current MVP).

For a concise architecture diagram and request flow, see [architecture.md](architecture.md). For route-level API tables, see [api-overview.md](api-overview.md).

## Technology stack

| Layer | Technology | Notes |
|--------|------------|--------|
| Runtime | **JDK 17** | Required for all JVM services |
| Framework | **Spring Boot 3.3.x** | Parent BOM in each service `pom.xml` |
| Cloud | **Spring Cloud 2023.0.x** | Gateway, Eureka client, load balancer |
| Gateway | **Spring Cloud Gateway** | Reactive; Redis rate limiting |
| Discovery | **Netflix Eureka** | Service registry |
| HTTP client | **OpenFeign** | Order → Product internal calls |
| Database | **PostgreSQL 16** | Single instance, multiple schemas |
| Migrations | **Flyway** | Per-service migrations under `classpath:db/migration` |
| Caching / limits | **Redis 7** | Used **only** by the API gateway for rate limiting |
| Frontend | **Angular 19** + **Angular Material** | SPA; production build served by nginx in Docker |
| Security | **JWT** (JJWT) | Shared signing secret across gateway and services that validate tokens |

## Repository layout (backend and UI)

| Path | Role |
|------|------|
| `discovery-server/` | Eureka server; other services register here |
| `api-gateway/` | Public entrypoint; routing, CORS, Redis rate limits |
| `auth-service/` | Registration, login, JWT issuance, user profile |
| `product-service/` | Catalog, categories, stock; internal inventory API |
| `order-service/` | Checkout and order history; calls product via Feign |
| `frontend/` | Angular SPA (`ng serve` or Docker nginx) |
| `docker-compose.yml` | Full stack: Postgres, Redis, all JVM services, frontend |

There is no Maven aggregator `pom.xml` at the repo root; each JVM module is built independently.

## Runtime topology and ports

When running via Docker Compose (default):

| Component | Host port | Responsibility |
|-----------|-----------|----------------|
| Angular (nginx) | **4200** | Static UI; proxies `/api/*` to the gateway |
| API Gateway | **8080** | All browser API traffic |
| Eureka | **8761** | Service discovery UI and registry |
| Auth | **8081** | `/api/auth/**` (routed through gateway in normal use) |
| Product | **8082** | Catalog and admin product APIs |
| Order | **8083** | Orders and checkout |
| PostgreSQL | **5432** | Shared DB, separate schemas per service |
| Redis | **6379** | Gateway rate limiter backend |

In Docker, the SPA uses a **relative** API base (`environment.prod.ts` empty `apiUrl`) so requests go to `/api/...` on the same origin and nginx forwards them to `api-gateway:8080`.

For local `ng serve`, `environment.ts` sets `apiUrl` to `http://localhost:8080` so the browser talks directly to the gateway.

## Data model and persistence

- **One PostgreSQL database** (`ecommerce` in the default Compose file) with **logical separation by schema**:
  - `auth_schema` — users and roles
  - `product_schema` — categories, products, inventory
  - `order_schema` — orders and line items
- **Flyway** runs per service on startup (`spring.flyway` in each service’s `application.yml`).
- JPA **`ddl-auto: validate`** — schema is owned by migrations, not auto-DDL at runtime.

## Security model

### Browser and gateway

- End users obtain a **Bearer JWT** from `POST /api/auth/login` (via gateway).
- Protected routes require a valid JWT; the gateway and downstream services are configured to trust the same **HS256** secret (`jwt.secret` / `JWT_SECRET`). The secret must be **at least 32 bytes**; the gateway enforces this when resolving rate-limit keys from tokens.
- **Roles** (for example `ROLE_USER`, `ROLE_ADMIN`) are carried in the token and used for admin-only catalog mutations and elevated order listing behavior.

### Internal service authentication

- **Order → Product**: HTTP calls to product’s **`/internal/*`** endpoints use header **`X-Service-Token`** matching `SERVICE_INTERNAL_TOKEN`. These paths are not intended for browsers.
- **Gateway rate-limit bypass**: Requests carrying **`X-Gateway-Internal-Token`** equal to `GATEWAY_INTERNAL_BYPASS_SECRET` match dedicated high-priority routes and **skip** Redis rate limiting. This header must **never** be exposed to client-side code; it is for trusted automation or internal tooling only.

### Seeded admin (auth service)

Default bootstrap credentials are documented in the main README; they can be overridden with `ADMIN_EMAIL` and `ADMIN_INITIAL_PASSWORD`.

## API gateway behavior

- **Routing**: Paths under `/api/auth/**`, `/api/products/**`, `/api/categories/**`, and `/api/orders/**` are routed to the corresponding Eureka-registered services using `lb://...` URIs.
- **Rate limiting**: Implemented with **Spring Cloud Gateway** `RequestRateLimiter` and **Redis**. Multiple `RedisRateLimiter` beans exist (for example stricter limits on auth, read-heavy catalog GETs, writes, and orders). The **key** is derived from:
  - bypass header match → fixed internal bucket,
  - else valid JWT subject → per-user bucket,
  - else client IP (with `X-Forwarded-For` support).
- **CORS**: Gateway docker profile supports configurable allowed origins (`CORS_ALLOWED_ORIGINS`).

## Service discovery and load balancing

- Services register with **Eureka** (`spring.application.name` per module).
- The gateway uses the **Spring Cloud LoadBalancer** with the Eureka discovery client to resolve `lb://auth-service`, `lb://product-service`, and `lb://order-service`.

## Checkout and inventory (order ↔ product)

Checkout is **synchronous**: the order service validates the cart, then calls the product service’s **`POST /internal/inventory/commit`** via **OpenFeign** (`lb://product-service`) with `X-Service-Token`. Stock is decremented in a transactional step before the order is persisted, giving a simple consistency model without a message broker.

## Frontend application

- **Angular 19** with standalone components and **Angular Material**.
- **API URL resolution**: `AuthService.api()` builds URLs from `environment.apiUrl` — absolute base for local dev, empty for production behind nginx (same-origin `/api`).
- **Auth storage**: Access token in **sessionStorage**; an HTTP interceptor attaches `Authorization: Bearer ...` where required.
- **Guards**: Route guards enforce authentication and admin-only areas.

## Operations and observability

- Each Spring Boot app exposes **Actuator** endpoints (health is used by Docker Compose health checks where configured).
- **Swagger UI** is available on individual JVM services at `/swagger-ui.html` when running them directly on their mapped ports (see [api-overview.md](api-overview.md)).

## Configuration reference (environment variables)

See [`.env.example`](../.env.example) for Compose-oriented variables: database, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `SERVICE_INTERNAL_TOKEN`, `GATEWAY_INTERNAL_BYPASS_SECRET`, and `CORS_ALLOWED_ORIGINS`.

## Related documents

| Document | Content |
|----------|---------|
| [architecture.md](architecture.md) | Mermaid diagram and request flow |
| [api-overview.md](api-overview.md) | HTTP API tables |
| [render-deployment.md](render-deployment.md) | Cloud deployment notes |
| [INSTALL_AND_RUN.md](INSTALL_AND_RUN.md) | Prerequisites, installs, and run commands |

## License

See the main [README.md](../README.md) for license statement.
