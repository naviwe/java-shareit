CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(128),
    name  VARCHAR(64),
    CONSTRAINT UNIQUE_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255),
    requestor_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    available   boolean,
    owner_id    BIGINT,
    request_id  BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (request_id) REFERENCES item_requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "start"   TIMESTAMP WITHOUT TIME ZONE,
    "end"     TIMESTAMP WITHOUT TIME ZONE,
    item_id   BIGINT,
    booker_id BIGINT,
    status    varchar(32),
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR(255),
    item_id   BIGINT,
    author_id BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);