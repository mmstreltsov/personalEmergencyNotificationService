CREATE TABLE incidents(
    userId     BIGINT NOT NULL,
    incidentId BIGINT PRIMARY KEY,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION
);
