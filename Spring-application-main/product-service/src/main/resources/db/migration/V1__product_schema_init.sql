CREATE SCHEMA IF NOT EXISTS product_schema;

CREATE TABLE product_schema.categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE product_schema.products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(12, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_url VARCHAR(1024),
    category_id BIGINT NOT NULL REFERENCES product_schema.categories (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_category ON product_schema.products (category_id);
CREATE INDEX idx_products_name_lower ON product_schema.products (LOWER(name));
