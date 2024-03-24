CREATE TABLE IF NOT EXISTS link
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    url             VARCHAR(255)             NOT NULL,
    last_check_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    primary key (id),
    unique (url)
);
