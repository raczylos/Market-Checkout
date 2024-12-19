CREATE TABLE PRODUCT (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    normal_price INTEGER NOT NULL,
    required_quantity INTEGER NOT NULL,
    special_price INTEGER NOT NULL
);