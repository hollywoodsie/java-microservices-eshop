CREATE TABLE eshop_user (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR(15) NOT NULL,
                         email VARCHAR(255),
                         password VARCHAR(100) NOT NULL,
                         roles VARCHAR(255) NOT NULL
);