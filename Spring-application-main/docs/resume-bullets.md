# Resume-ready bullets

- Built a **Spring Cloud** microservices storefront (**Auth**, **Product**, **Order**) behind a **Spring Cloud Gateway** with **Eureka** service discovery and **OpenFeign** inter-service calls.
- Implemented **JWT authentication** with **role-based access** (USER/ADMIN), BCrypt password storage, and **resource-server JWT validation** in domain services.
- Added **Redis-backed rate limiting** at the gateway with differentiated policies (auth vs public reads vs writes vs orders) and **IP + user-based keys**, plus a **trusted internal bypass** header for safe operator/automation traffic.
- Designed **PostgreSQL** with **separate schemas per service** and **Flyway** migrations; implemented **checkout** with an **atomic inventory commit** API to avoid partial stock updates in the happy path.
- Delivered an **Angular 19 + Material** SPA with **lazy routes**, **route guards**, an **HTTP interceptor** for JWT/429 handling, and an **nginx** production container proxying `/api` to the gateway.
