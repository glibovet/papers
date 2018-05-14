CREATE TABLE IF NOT EXISTS `papers`.`dictionary` (
  `word` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`word`));

CREATE TABLE IF NOT EXISTS `papers`.`publications_cosine_similarity` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `publication1_id` INT NULL,
  `publication2_id` INT NULL,
  `value` DOUBLE NULL,
  PRIMARY KEY (`id`),
  INDEX `publication1_id_idx` (`publication1_id` ASC),
  INDEX `publication2_id_idx` (`publication2_id` ASC),
  CONSTRAINT `publication1_id`
    FOREIGN KEY (`publication1_id`)
    REFERENCES `papers`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `publication2_id`
    FOREIGN KEY (`publication2_id`)
    REFERENCES `papers`.`publication` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
