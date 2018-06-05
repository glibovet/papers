CREATE TABLE IF NOT EXISTS `papers`.`chat` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `initiator_user_id` INT(11) NOT NULL,
  INDEX `fk_chat_to_user_idx` (`initiator_user_id` ASC),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_chat_to_initiator_user`
    FOREIGN KEY (`initiator_user_id`)
    REFERENCES `papers`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `papers`.`user_to_chat` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `chat_id` INT(11) NOT NULL,
  `has_unread_messages` TINYINT(1) NOT NULL DEFAULT 0,
  INDEX `user_id_idx` (`user_id` ASC),
  INDEX `chat_id_idx` (`chat_id` ASC),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_chat_to_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `papers`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_to_chat`
    FOREIGN KEY (`chat_id`)
    REFERENCES `papers`.`chat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `papers`.`message` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `chat_id` INT(11) NOT NULL,
  `text` TEXT NOT NULL,
  `date` DATETIME NOT NULL,
  `attachment` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `message_to_user_idx` (`user_id` ASC),
  INDEX `fk_message_to_chat_idx` (`chat_id` ASC),
  CONSTRAINT `fk_message_to_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `papers`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_message_to_chat`
  FOREIGN KEY (`chat_id`)
  REFERENCES `papers`.`chat` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

  CREATE TABLE `papers`.`interest` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `papers`.`company` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NULL,
  `domain` VARCHAR(200) NULL,
  `site` VARCHAR(200) NULL,
  `about` TEXT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `papers`.`users`
  ADD `last_name` VARCHAR(45);

ALTER TABLE `papers`.`users`
  ADD `photo` VARCHAR(500);

INSERT INTO `papers`.`role` (`name`) VALUES ('student');
INSERT INTO `papers`.`role` (`name`) VALUES ('scientist');

INSERT INTO `papers`.`permissions` (`name`) VALUES ('ROLE_SCIENTIST');

INSERT INTO `papers`.`role_permissions` (`role_id`, `permission_id`) VALUES (4,1);
INSERT INTO `papers`.`role_permissions` (`role_id`, `permission_id`) VALUES (5,1);
INSERT INTO `papers`.`role_permissions` (`role_id`, `permission_id`) VALUES (5,4);

CREATE TABLE `papers`.`contacts` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_from` INT(11) NOT NULL,
  `user_to` INT(11) NOT NULL,
  `isAccepted` TINYINT(1) NOT NULL,
  `message` VARCHAR(500) NULL,
  `attachment` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_from_user_to_user_idx` (`user_from` ASC),
  INDEX `fk_to_user_to_user_idx` (`user_to` ASC),
  CONSTRAINT `fk_from_user_to_user`
  FOREIGN KEY (`user_from`)
  REFERENCES `papers`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_to_user_to_user`
  FOREIGN KEY (`user_to`)
  REFERENCES `papers`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);






