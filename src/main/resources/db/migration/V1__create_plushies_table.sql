CREATE TABLE plushies
(
    id    SERIAL PRIMARY KEY,
    name  TEXT    NOT NULL,
    price NUMERIC NOT NULL,
    quantity NUMERIC NOT NULL
);