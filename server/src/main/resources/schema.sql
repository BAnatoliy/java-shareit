DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    email       VARCHAR(50),
    name        VARCHAR(50),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name            VARCHAR(50),
    description     VARCHAR(200),
    available       BOOLEAN,
    user_id         BIGINT,
    last_booking_id BIGINT,
    next_booking_id BIGINT,
    request_id      BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_booking TIMESTAMP,
    end_booking   TIMESTAMP,
    status        VARCHAR(8),
    user_id       BIGINT,
    item_id       BIGINT
);

CREATE TABLE IF NOT EXISTS comments (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text          VARCHAR(250),
    created       TIMESTAMP,
    user_id       BIGINT,
    item_id       BIGINT
);

CREATE TABLE IF NOT EXISTS requests (
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description   VARCHAR(250),
    created       TIMESTAMP,
    user_id       BIGINT
);

ALTER TABLE IF EXISTS items ADD CONSTRAINT fk_item_to_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE IF EXISTS items ADD CONSTRAINT fk_item_to_last_booking FOREIGN KEY(last_booking_id) REFERENCES bookings(id);
ALTER TABLE IF EXISTS items ADD CONSTRAINT fk_item_to_next_booking FOREIGN KEY(next_booking_id) REFERENCES bookings(id);
ALTER TABLE IF EXISTS items ADD CONSTRAINT fk_item_to_request FOREIGN KEY(request_id) REFERENCES requests(id);
ALTER TABLE IF EXISTS bookings ADD CONSTRAINT fk_booking_to_item FOREIGN KEY(item_id) REFERENCES items(id);
ALTER TABLE IF EXISTS bookings ADD CONSTRAINT fk_booking_to_user FOREIGN KEY(user_id) REFERENCES users(id);
ALTER TABLE IF EXISTS comments ADD CONSTRAINT fk_comment_to_item FOREIGN KEY(item_id) REFERENCES items(id);
ALTER TABLE IF EXISTS comments ADD CONSTRAINT fk_comment_to_user FOREIGN KEY(user_id) REFERENCES users(id);
ALTER TABLE IF EXISTS requests ADD CONSTRAINT fk_request_to_user FOREIGN KEY(user_id) REFERENCES users(id);