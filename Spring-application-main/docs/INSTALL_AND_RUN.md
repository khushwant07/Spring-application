# Install dependencies and run the application

This guide covers prerequisites, installing dependencies, and running the **E-Commerce microservices** project located in **`Spring-application-main`**.

## Prerequisites

Install the following on your machine:

| Tool | Version | Used for |
|------|---------|----------|
| **JDK** | 17 | All Spring Boot services |
| **Maven** | 3.9+ | Building and running JVM modules |
| **Node.js** | 20+ | Angular frontend |
| **npm** | Bundled with Node | Frontend dependencies |
| **Docker Desktop** | Current stable | Recommended full stack via Compose |

Optional:

- **curl** — some Compose health checks call `curl` inside containers.

## Where the application lives

All runnable services and `docker-compose.yml` are under:

```text
Spring-application-main/
```

Commands below assume your shell’s current directory is **`Spring-application-main`** unless noted otherwise.

> **Note:** If your workspace root is `E-Commerce` and you see a separate `package.json` there with only `@angular/cli`, that file is **not** required to run this application. The real frontend is in `Spring-application-main/frontend/`.

---

## Option A: Full stack with Docker Compose (recommended)

This builds and starts PostgreSQL, Redis, Eureka, auth, product, order, API gateway, and the Angular app (nginx on port 4200).

### 1. Optional: environment file

Copy the example env file and edit secrets for anything beyond local demo use:

```bash
cd Spring-application-main
cp .env.example .env
```

Compose substitutes variables from `.env` when present. If you skip this step, defaults from `docker-compose.yml` apply.

### 2. Start everything

```bash
cd Spring-application-main
docker compose up --build
```

First run downloads images and builds all services; it may take several minutes.

### 3. Open the app

| What | URL |
|------|-----|
| **Web UI (Angular via nginx)** | http://localhost:4200 |
| **API Gateway** (direct) | http://localhost:8080 |
| **Eureka dashboard** | http://localhost:8761 |

Individual services are also published on **8081** (auth), **8082** (product), **8083** (order) for debugging.

### 4. Default admin login

After the auth service starts and seeds data:

- **Email:** `admin@ecommerce.local` (override with `ADMIN_EMAIL` in Compose/env)
- **Password:** `Admin123!` (override with `ADMIN_INITIAL_PASSWORD`)

### 5. Stop the stack

Press `Ctrl+C` in the terminal, or from the same directory:

```bash
docker compose down
```

To remove the Postgres volume as well:

```bash
docker compose down -v
```

---

## Option B: Local frontend with Docker (or existing) backend

Use this when the **API gateway** is already reachable at **http://localhost:8080** (for example after Compose is up, or if you run the gateway yourself).

### 1. Install frontend dependencies

```bash
cd Spring-application-main/frontend
npm install
```

If `npm install` reports peer dependency issues, the project’s Docker build uses `npm install --legacy-peer-deps`; you can use the same flag locally if needed:

```bash
npm install --legacy-peer-deps
```

### 2. Run the dev server

```bash
npm start
```

This runs `ng serve`. Open **http://localhost:4200**.

The dev environment file `src/environments/environment.ts` points `apiUrl` to **http://localhost:8080**, so the browser calls the gateway directly (no nginx proxy in this mode).

---

## Option C: Backend only (no Docker) — overview

Running every JVM service without Docker is possible but more manual: you need **PostgreSQL** and **Redis** running locally with credentials matching each service’s `application.yml` (defaults align with Compose: database `ecommerce`, user/password `ecommerce`, Redis `localhost:6379`).

Typical order:

1. Start **PostgreSQL** and **Redis**.
2. Start **discovery-server** (Eureka):  
   `cd Spring-application-main/discovery-server && mvn spring-boot:run`
3. Start **auth-service**, **product-service**, **order-service** (each in its own terminal):  
   `mvn spring-boot:run` from each module directory.
4. Start **api-gateway** last so upstream services are registered.

Ensure **`JWT_SECRET`** (and any other secrets) are set consistently in the environment for all services that validate JWTs and for the gateway.

For day-to-day development, **Option A** or **Option B** is usually faster.

---

## Building artifacts without running

### Spring Boot (per module)

```bash
cd Spring-application-main/<module-name>
mvn -q -DskipTests package
```

Replace `<module-name>` with `discovery-server`, `auth-service`, `product-service`, `order-service`, or `api-gateway`.

### Angular production build

```bash
cd Spring-application-main/frontend
npm install
npm run build
```

Output goes under `frontend/dist/` (exact subfolder depends on Angular version and `angular.json`).

---

## Troubleshooting

| Issue | What to check |
|--------|----------------|
| Port already in use | Stop other Postgres/Redis/Spring processes or change host ports in `docker-compose.yml`. |
| Services unhealthy in Compose | Wait for Eureka and Postgres health; inspect logs: `docker compose logs <service-name>`. |
| Frontend cannot reach API | With Compose UI, use port **4200** (nginx proxies `/api`). With `ng serve`, ensure gateway is on **8080** and `environment.ts` matches. |
| 401 / invalid token | Same `JWT_SECRET` must be used across gateway and services in your environment. |

---

## More documentation

- [TECHNICAL_DOCUMENTATION.md](TECHNICAL_DOCUMENTATION.md) — architecture and technical details  
- [architecture.md](architecture.md) — diagram and request flow  
- [api-overview.md](api-overview.md) — API reference  
