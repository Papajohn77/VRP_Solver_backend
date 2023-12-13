CREATE TABLE models (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE depots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    address VARCHAR(255) NOT NULL,
    model_id INTEGER REFERENCES models(id) ON DELETE CASCADE UNIQUE NOT NULL
);

CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    demand INTEGER NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    address VARCHAR(255) NOT NULL,
    model_id INTEGER REFERENCES models(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    model_id INTEGER REFERENCES models(id) ON DELETE CASCADE NOT NULL
);

CREATE INDEX depots_fk_idx ON depots(model_id);
CREATE INDEX customers_fk_idx ON customers(model_id);
CREATE INDEX vehicles_fk_idx ON vehicles(model_id);
