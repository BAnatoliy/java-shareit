DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS items;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    email       VARCHAR(50),
    name        VARCHAR(50),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name        VARCHAR(50),
    description VARCHAR(200),
    available   BOOLEAN,
    user_id     BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

