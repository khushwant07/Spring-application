# API overview (via gateway)

Base URL examples:

- Local gateway: `http://localhost:8080`
- Docker nginx SPA: `http://localhost:4200/api/...` (proxied to gateway)

## Auth (`/api/auth/**`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | Public | Create user (ROLE_USER) |
| POST | `/api/auth/login` | Public | Returns JWT (`accessToken`) |
| GET | `/api/auth/me` | Bearer | Current profile |

## Catalog (`/api/products/**`, `/api/categories/**`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/products` | Public | Paginated list (`page`, `size`, `q`, `categoryId`) |
| GET | `/api/products/{id}` | Public | Product detail |
| GET | `/api/categories` | Public | Categories |
| POST/PUT/DELETE | `/api/categories/**` | Bearer + ADMIN | Category CRUD |
| POST/PUT/PATCH/DELETE | `/api/products/**` | Bearer + ADMIN | Product CRUD / inventory patch |

## Orders (`/api/orders/**`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/orders` | Bearer | Checkout body: `{ "lines": [ { "productId": 1, "quantity": 2 } ] }` |
| GET | `/api/orders` | Bearer | Order history (admin: recent global list) |
| GET | `/api/orders/{id}` | Bearer | Order detail (owner or admin) |

## Internal (product service, not via public UI)

| Method | Path | Header | Description |
|--------|------|--------|-------------|
| GET | `/internal/products/{id}` | `X-Service-Token` | Snapshot for services |
| POST | `/internal/inventory/commit` | `X-Service-Token` | Atomic multi-line decrement |

Swagger UI (local ports): `/swagger-ui.html` on each JVM service.
