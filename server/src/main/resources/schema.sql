CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(200)                            NOT NULL,
    email   VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(250)                            NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    FOREIGN KEY (requester_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_request PRIMARY KEY (request_id)
);
CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(200)                            NOT NULL,
    description VARCHAR(250)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    FOREIGN KEY (owner_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    FOREIGN KEY (request_id)
        REFERENCES requests (request_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS bookings
(
    booking_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date       TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id        BIGINT                                  NOT NULL,
    booker_id      BIGINT,
    booking_status VARCHAR(20),
    FOREIGN KEY (item_id)
        REFERENCES items (item_id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_booking PRIMARY KEY (booking_id)
);
CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text       VARCHAR(500)                            NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    author_id  BIGINT,
    created    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    FOREIGN KEY (item_id)
        REFERENCES items (item_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_comment PRIMARY KEY (comment_id)
);