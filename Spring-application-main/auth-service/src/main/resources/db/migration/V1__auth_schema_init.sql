CREATE SCHEMA IF NOT EXISTS auth_schema;

CREATE TABLE auth_schema.roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE auth_schema.users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE auth_schema.user_roles (
    user_id BIGINT NOT NULL REFERENCES auth_schema.users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES auth_schema.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_users_email ON auth_schema.users (email);
