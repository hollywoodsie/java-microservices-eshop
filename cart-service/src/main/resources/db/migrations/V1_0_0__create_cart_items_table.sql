CREATE TABLE cart_items (
                         id SERIAL PRIMARY KEY,
                         product_id BIGINT NOT NULL,
                         owner_id BIGINT NOT NULL,
                         name VARCHAR(10) NOT NULL,
                         description VARCHAR(500) NOT NULL,
                         price NUMERIC(19, 2) NOT NULL
);