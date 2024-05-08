CREATE TABLE product (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(20) NOT NULL,
                         description VARCHAR(500) NOT NULL,
                         price DECIMAL(19, 2) NOT NULL
);