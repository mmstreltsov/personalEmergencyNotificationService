CREATE INDEX idx_first_time_to_activate
    ON Scenario (firstTimeToActivate)
    WHERE scenario.firsttimetoactivate < 'infinity'::timestamp;

CREATE INDEX scenarios_chat_ids
    ON Scenario (clientId, name);

CREATE INDEX scenarios_uuid
    ON Scenario (uuid);

CREATE INDEX clients_telegram_id
    ON Clients (telegramId);
