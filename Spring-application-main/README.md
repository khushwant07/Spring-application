# E-Commerce Microservices (Portfolio)

**Designed to demonstrate:**

- Microservices architecture
- API Gateway patterns
- Distributed authentication
- Rate limiting with Redis
- Secure role-based APIs
- Dockerized deployment
- Full stack Angular + Spring Boot development

**Production-oriented e-commerce microservices platform** built with **Spring Cloud**, **Eureka**, **API Gateway** (JWT-aware keys, **Redis** rate limits, internal bypass header), **PostgreSQL** (per-service schemas), **OpenFeign** checkout, and an **Angular 19 + Material** SPA.

## Documentation

- **[Technical documentation](docs/TECHNICAL_DOCUMENTATION.md)** — stack, data, security, gateway, and frontend behavior.
- **[Install and run](docs/INSTALL_AND_RUN.md)** — prerequisites, dependency install, Docker Compose, and local development.

## Prerequisites

- **JDK 17** and Maven 3.9+
- **Node.js 20+** and npm (for the Angular app)
- **Docker Desktop** (optional, for Compose)

## Quick start (Docker Compose)

From this directory:

```bash
docker compose up --build
```

Services:

| Service           | URL                          |
|-------------------|------------------------------|
| Angular (nginx)   | http://localhost:4200        |
| API Gateway       | http://localhost:8080        |
| Eureka            | http://localhost:8761        |
| Auth              | http://localhost:8081        |
| Product           | http://localhost:8082        |
| Order             | http://localhost:8083        |
| PostgreSQL        | localhost:5432               |
| Redis             | localhost:6379               |

The SPA calls the gateway via **nginx** (`/api/...` → `api-gateway:8080`). Local `ng serve` uses `environment.ts` and points to `http://localhost:8080`.

### Default admin user (auth service seed)

- **Email:** `admin@ecommerce.local` (override with `ADMIN_EMAIL`)
- **Password:** `Admin123!` (override with `ADMIN_INITIAL_PASSWORD`)

Copy [`.env.example`](.env.example) to `.env` if you want to customize secrets for Compose.

## Local development (without Docker for the UI)

1. Start infrastructure and JVM services as you prefer (Compose is easiest).
2. `cd frontend && npm install && npm start`
3. Open http://localhost:4200

## Architecture

See [docs/architecture.md](docs/architecture.md) and the Mermaid diagram inside it.

## API overview

See [docs/api-overview.md](docs/api-overview.md).

## Deployment (Render)

See [docs/render-deployment.md](docs/render-deployment.md).

## Screenshots (for your README / resume)

Add images under `docs/screenshots/` (see [docs/screenshots/README.md](docs/screenshots/README.md)).

## Resume bullets

See [docs/resume-bullets.md](docs/resume-bullets.md).

## Future improvements

See [docs/future-improvements.md](docs/future-improvements.md).

## Engineering notes

- **Java 17**, Spring Boot **3.3.x**, Spring Cloud **2023.0.x**
- **Redis is only used by the API Gateway** (rate limiting), not by domain services
- **Internal gateway rate-limit bypass:** header `X-Gateway-Internal-Token` must match `GATEWAY_INTERNAL_BYPASS_SECRET` (never expose to browsers)
- **Service-to-service calls:** Order → Product uses **Feign** to `lb://product-service` with `X-Service-Token` (`SERVICE_INTERNAL_TOKEN`), not through the public gateway buckets

## License

MIT (or replace with your preferred license for a portfolio repo).
