CREATE TABLE Clients (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    telegramId VARCHAR(255) UNIQUE,
    chatId BIGINT,
    listOfFriends JSONB
);
--  {
--    "id": <integer>,
--    "name": <string>,
--    "wayToNotify": [
--      <string>
--    ],
--    "phoneNumber": <string>,
--    "telegramId": <integer>,
--    "chatId": <integer>,
--    "email": <string>
--  }

