CREATE TABLE users
(
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(128),
    name  VARCHAR(64)
);

CREATE TABLE item_requests
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    description  VARCHAR(255),
    requestor_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE items
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255),
    description  VARCHAR(255),
    available    boolean,
    owner_id     BIGINT,
    requester_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (requester_id) REFERENCES item_requests (id)
);

CREATE TABLE bookings
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    `start`   TIMESTAMP WITHOUT TIME ZONE,
    `end`     TIMESTAMP WITHOUT TIME ZONE,
    item_id   BIGINT,
    booker_id BIGINT,
    status    varchar(32),
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE comments
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    text      VARCHAR(255),
    item_id   BIGINT,
    author_id BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);