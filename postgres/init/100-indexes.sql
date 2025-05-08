CREATE INDEX idx_first_time_to_activate
    ON Scenario (firstTimeToActivate)

CREATE INDEX scenarios_chat_ids
    ON Scenario (clientId, name);

CREATE INDEX clients_telegram_id
    ON Clients (telegramId);
