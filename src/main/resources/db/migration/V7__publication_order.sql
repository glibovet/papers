-- -----------------------------------------------------
-- Table `papers`.`publication_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `papers`.`publication_order` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '',
  `email` VARCHAR(50) NOT NULL COMMENT '',
  `publication_id` INT NOT NULL COMMENT '',
  `reason` TEXT NOT NULL COMMENT '',
  `answer` TEXT NULL COMMENT '',
  `status` VARCHAR(45) NOT NULL COMMENT '',
  `date_created` datetime NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `fk_publication_order_to_publication_idx` (`publication_id` ASC)  COMMENT '',
  CONSTRAINT `fk_publication_order_to_publication`
    FOREIGN KEY (`publication_id`)
    REFERENCES `papers`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;