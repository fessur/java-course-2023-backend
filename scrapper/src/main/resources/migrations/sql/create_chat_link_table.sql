CREATE TABLE IF NOT EXISTS chat_link
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id BIGINT NOT NULL,
    link_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (link_id) REFERENCES link (id)
);
