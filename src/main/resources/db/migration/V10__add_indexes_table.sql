CREATE TABLE `papers`.`indexes` IF NOT EXISTS (`url` VARCHAR(320) NOT NULL PRIMARY KEY, `last_visit` TIMESTAMP, `content_hash` VARCHAR (32));
