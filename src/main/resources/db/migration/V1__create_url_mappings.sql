CREATE TABLE url_mappings (
                              id          BIGSERIAL       PRIMARY KEY,
                              short_code  VARCHAR(10)     NOT NULL UNIQUE,
                              long_url    TEXT            NOT NULL,
                              created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
                              click_count BIGINT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_short_code ON url_mappings(short_code);