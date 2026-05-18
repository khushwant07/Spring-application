# Deploying on Render

This stack is designed to be **economical** and **explainable**: one Eureka, one gateway, three domain services, one Angular static site, managed Postgres + Redis.

## Services to create

Create **one Web Service per** JVM app and the frontend:

1. `discovery-server` (Dockerfile in `discovery-server/`)
2. `api-gateway` (Dockerfile in `api-gateway/`)
3. `auth-service`
4. `product-service`
5. `order-service`
6. `frontend` (Dockerfile in `frontend/`)

Create **PostgreSQL** and **Redis** add-ons (or external providers). Point all JDBC URLs at the same database with database name `ecommerce` (or your choice) so the three schemas can coexist.

## Environment variables (representative)

### All JVM services

- `SPRING_PROFILES_ACTIVE=docker` (or a `render` profile you add)
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://<your-eureka-host>/eureka/`
- `JWT_SECRET=<at-least-32-bytes-secret>`

### Auth

- `ADMIN_EMAIL`, `ADMIN_INITIAL_PASSWORD` (optional overrides)

### Product + Order

- `SERVICE_INTERNAL_TOKEN=<shared-secret>` (must match between services)

### Gateway only

- `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`, `SPRING_DATA_REDIS_PASSWORD` (if required)
- `GATEWAY_INTERNAL_BYPASS_SECRET=<long-random-secret>`
- `CORS_ALLOWED_ORIGINS=https://<your-frontend-host>`

### Frontend build

Production build uses `environment.prod.ts` with `apiUrl: ''` so the browser calls **same-origin** `/api/...` and nginx proxies to the gateway URL inside the private network.

Update `frontend/nginx.conf` `proxy_pass` target if your gateway service hostname on Render differs from `api-gateway`.

## Health checks

Use each service `GET /actuator/health` as the Render health check path (Eureka included).

## Cold starts

Free/low tiers may sleep. Expect Eureka re-registration delays after wake; keep instances minimal and document this in interviews.

## Optional

Add a `render.yaml` Blueprint once URLs stabilize for reproducible redeploys.
