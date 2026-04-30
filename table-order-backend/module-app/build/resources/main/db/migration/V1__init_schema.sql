-- V1__init_schema.sql
-- Table Order Service - Initial Schema

CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (store_id, username)
);

CREATE TABLE tables (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    table_number INT NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (store_id, table_number)
);

CREATE TABLE table_sessions (
    id BIGSERIAL PRIMARY KEY,
    table_id BIGINT NOT NULL REFERENCES tables(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    name VARCHAR(50) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE menus (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL CHECK (price >= 0),
    description VARCHAR(500),
    image_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL REFERENCES stores(id),
    table_id BIGINT NOT NULL REFERENCES tables(id),
    session_id BIGINT NOT NULL REFERENCES table_sessions(id),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount INT NOT NULL CHECK (total_amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    menu_id BIGINT NOT NULL REFERENCES menus(id),
    menu_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 1),
    unit_price INT NOT NULL CHECK (unit_price >= 0),
    subtotal INT NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE order_history (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL,
    total_amount INT NOT NULL,
    items TEXT NOT NULL,
    ordered_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_admins_store_username ON admins(store_id, username);
CREATE INDEX idx_tables_store ON tables(store_id);
CREATE INDEX idx_table_sessions_table_status ON table_sessions(table_id, status);
CREATE INDEX idx_categories_store_order ON categories(store_id, display_order);
CREATE INDEX idx_menus_store_category_order ON menus(store_id, category_id, display_order);
CREATE INDEX idx_orders_store_session ON orders(store_id, session_id);
CREATE INDEX idx_orders_store_status ON orders(store_id, status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_history_table_completed ON order_history(table_id, completed_at);
