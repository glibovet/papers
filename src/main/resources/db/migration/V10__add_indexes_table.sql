CREATE TABLE IF NOT EXISTS `papers`.`indexes` (`url` VARCHAR(320) NOT NULL PRIMARY KEY, `last_visit` TIMESTAMP, `content_hash` VARCHAR (32));
