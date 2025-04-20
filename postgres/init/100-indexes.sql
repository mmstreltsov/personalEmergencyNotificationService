CREATE INDEX idx_first_time_to_activate
    ON Scenario (firstTimeToActivate)
    WHERE scenario.firsttimetoactivate < 'infinity'::timestamp;