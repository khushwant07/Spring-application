# Future improvements (not in MVP)

These are common “next steps” to mention in interviews without bloating the codebase today.

## Payments & commerce

- Payment provider integration (Stripe/PayPal) with **idempotent** checkout and payment state machine
- **Tax/shipping** quotes, coupons, and promotions

## Reliability & async

- **Outbox pattern** + message broker (**Kafka/RabbitMQ**) for reliable notifications and inventory reconciliation
- **Saga / compensation** flows if you need multi-service atomicity beyond the current single commit API
- **Circuit breakers** (Resilience4j) and bulkheads around Feign clients

## Security

- **Refresh tokens** + rotation, optional **JWT denylist** stored in **Redis at the gateway only**
- **mTLS** or signed service-to-service tokens for internal endpoints

## Observability

- **OpenTelemetry** traces across gateway + services
- Structured logging + **ELK/OpenSearch** or a hosted log platform
- Lightweight **metrics** (Micrometer + OTLP) without running a full Prometheus/Grafana stack locally

## Frontend

- Deeper admin workflows (image upload, rich editor), e2e tests (**Playwright**), accessibility audit fixes
