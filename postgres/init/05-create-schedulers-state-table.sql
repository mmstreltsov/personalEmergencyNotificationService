CREATE TABLE schedulers
(
    id        BIGINT PRIMARY KEY,
    fetchTime TIMESTAMP NOT NULL,
    successLastTry BOOLEAN DEFAULT TRUE
);
