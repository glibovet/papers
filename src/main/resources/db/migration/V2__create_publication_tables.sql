-- -----------------------------------------------------
-- Table `papers`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`address` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `country` VARCHAR(100) NULL COMMENT '',
  `city` VARCHAR(100) NULL COMMENT '',
  `address` VARCHAR(500) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `papers`.`publisher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`publisher` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `title` VARCHAR(500) NULL COMMENT '',
  `description` TEXT NULL COMMENT '',
  `URL` VARCHAR(200) NULL COMMENT '',
  `contacts` TEXT NULL COMMENT '',
  `address_id` INT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `fk_publisher_to_address_idx` (`address_id` ASC)  COMMENT '',
  CONSTRAINT `fk_publisher_to_address`
  FOREIGN KEY (`address_id`)
  REFERENCES `papers`.`address` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `papers`.`publication`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`publication` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `title` VARCHAR(500) NULL COMMENT '',
  `annotation` TEXT NULL COMMENT '',
  `type` VARCHAR(45) NULL COMMENT '',
  `link` VARCHAR(500) NULL COMMENT '',
  `publisher_id` INT NULL COMMENT '',
  `in_index` TINYINT(1) NULL COMMENT '',
  `status` VARCHAR(45) NULL COMMENT '',
  `literature_parsed` TINYINT(1) NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `fk_pubisher_to_Publication_idx` (`publisher_id` ASC)  COMMENT '',
  CONSTRAINT `fk_pubisher_to_Publication`
  FOREIGN KEY (`publisher_id`)
  REFERENCES `papers`.`publisher` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `papers`.`author_master`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`author_master` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `last_name` VARCHAR(100) NOT NULL COMMENT '',
  `initials` VARCHAR(15) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '')
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `papers`.`author`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`author` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `last_name` VARCHAR(75) NULL COMMENT '',
  `initials` VARCHAR(45) NULL COMMENT '',
  `as_it` VARCHAR(250) NOT NULL COMMENT '',
  `author_master_id` INT NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `fk_author_to_master_author_idx` (`author_master_id` ASC)  COMMENT '',
  CONSTRAINT `fk_author_to_master_author`
  FOREIGN KEY (`author_master_id`)
  REFERENCES `papers`.`author_master` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `papers`.`author_to_publication`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`author_to_publication` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `publication_id` INT NOT NULL COMMENT '',
  `author_master_id` INT NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `fk_publication_to_author_idx` (`publication_id` ASC)  COMMENT '',
  INDEX `fk_author_to_publication_idx` (`author_master_id` ASC)  COMMENT '',
  CONSTRAINT `fk_publication_to_author`
  FOREIGN KEY (`publication_id`)
  REFERENCES `papers`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_author_to_publication`
  FOREIGN KEY (`author_master_id`)
  REFERENCES `papers`.`author_master` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;