CREATE TABLE IF NOT EXISTS `papers`.`stop_words_dictionary` (
  `word` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`word`));

-- http://meta-ukraine.com/ua/pages/stopwrd.asp
INSERT INTO stop_words_dictionary VALUES ("дещо");
INSERT INTO stop_words_dictionary VALUES ("авжеж");
INSERT INTO stop_words_dictionary VALUES ("тобто");
INSERT INTO stop_words_dictionary VALUES ("тощо");
INSERT INTO stop_words_dictionary VALUES ("тож");
INSERT INTO stop_words_dictionary VALUES ("отже");
INSERT INTO stop_words_dictionary VALUES ("отож");
INSERT INTO stop_words_dictionary VALUES ("як");
INSERT INTO stop_words_dictionary VALUES ("який");

-- http://www.marazm.org.ua/windows/50_141.html
INSERT INTO stop_words_dictionary VALUES ("один");
INSERT INTO stop_words_dictionary VALUES ("два");
INSERT INTO stop_words_dictionary VALUES ("три");
INSERT INTO stop_words_dictionary VALUES ("чотири");
INSERT INTO stop_words_dictionary VALUES ("п'ять");
INSERT INTO stop_words_dictionary VALUES ("шість");
INSERT INTO stop_words_dictionary VALUES ("сім");
INSERT INTO stop_words_dictionary VALUES ("вісім");
INSERT INTO stop_words_dictionary VALUES ("дев'ять");
INSERT INTO stop_words_dictionary VALUES ("нуль");

INSERT INTO stop_words_dictionary VALUES ("без");
INSERT INTO stop_words_dictionary VALUES ("більш");
INSERT INTO stop_words_dictionary VALUES ("більше");
INSERT INTO stop_words_dictionary VALUES ("буде");
INSERT INTO stop_words_dictionary VALUES ("начебто");
INSERT INTO stop_words_dictionary VALUES ("би");
INSERT INTO stop_words_dictionary VALUES ("був");
INSERT INTO stop_words_dictionary VALUES ("була");
INSERT INTO stop_words_dictionary VALUES ("були");
INSERT INTO stop_words_dictionary VALUES ("було");
INSERT INTO stop_words_dictionary VALUES ("бути");
INSERT INTO stop_words_dictionary VALUES ("вам");
INSERT INTO stop_words_dictionary VALUES ("вас");
INSERT INTO stop_words_dictionary VALUES ("адже");
INSERT INTO stop_words_dictionary VALUES ("увесь");
INSERT INTO stop_words_dictionary VALUES ("уздовж");
INSERT INTO stop_words_dictionary VALUES ("раптом");
INSERT INTO stop_words_dictionary VALUES ("замість");
INSERT INTO stop_words_dictionary VALUES ("поза");
INSERT INTO stop_words_dictionary VALUES ("униз");
INSERT INTO stop_words_dictionary VALUES ("унизу");
INSERT INTO stop_words_dictionary VALUES ("усередині");
INSERT INTO stop_words_dictionary VALUES ("в");
INSERT INTO stop_words_dictionary VALUES ("навколо");
INSERT INTO stop_words_dictionary VALUES ("от");
INSERT INTO stop_words_dictionary VALUES ("втім");
INSERT INTO stop_words_dictionary VALUES ("усі");
INSERT INTO stop_words_dictionary VALUES ("завжди");
INSERT INTO stop_words_dictionary VALUES ("усього");
INSERT INTO stop_words_dictionary VALUES ("усіх");
INSERT INTO stop_words_dictionary VALUES ("усю");
INSERT INTO stop_words_dictionary VALUES ("ви");
INSERT INTO stop_words_dictionary VALUES ("де");
INSERT INTO stop_words_dictionary VALUES ("так");
INSERT INTO stop_words_dictionary VALUES ("давай");
INSERT INTO stop_words_dictionary VALUES ("давати");
INSERT INTO stop_words_dictionary VALUES ("навіть");
INSERT INTO stop_words_dictionary VALUES ("для");
INSERT INTO stop_words_dictionary VALUES ("до");
INSERT INTO stop_words_dictionary VALUES ("досить");
INSERT INTO stop_words_dictionary VALUES ("інший");
INSERT INTO stop_words_dictionary VALUES ("його");
INSERT INTO stop_words_dictionary VALUES ("йому");
INSERT INTO stop_words_dictionary VALUES ("її");
INSERT INTO stop_words_dictionary VALUES ("їй");
INSERT INTO stop_words_dictionary VALUES ("якщо");
INSERT INTO stop_words_dictionary VALUES ("є");
INSERT INTO stop_words_dictionary VALUES ("ще");
INSERT INTO stop_words_dictionary VALUES ("же");
INSERT INTO stop_words_dictionary VALUES ("за");
INSERT INTO stop_words_dictionary VALUES ("за винятком");
INSERT INTO stop_words_dictionary VALUES ("тут");
INSERT INTO stop_words_dictionary VALUES ("з");
INSERT INTO stop_words_dictionary VALUES ("через");
INSERT INTO stop_words_dictionary VALUES ("або");
INSERT INTO stop_words_dictionary VALUES ("їм");
INSERT INTO stop_words_dictionary VALUES ("мати");
INSERT INTO stop_words_dictionary VALUES ("іноді");
INSERT INTO stop_words_dictionary VALUES ("їх");
INSERT INTO stop_words_dictionary VALUES ("якось");
INSERT INTO stop_words_dictionary VALUES ("хто");
INSERT INTO stop_words_dictionary VALUES ("коли");
INSERT INTO stop_words_dictionary VALUES ("крім");
INSERT INTO stop_words_dictionary VALUES ("куди");
INSERT INTO stop_words_dictionary VALUES ("чи");
INSERT INTO stop_words_dictionary VALUES ("між");
INSERT INTO stop_words_dictionary VALUES ("мене");
INSERT INTO stop_words_dictionary VALUES ("мені");
INSERT INTO stop_words_dictionary VALUES ("багато");
INSERT INTO stop_words_dictionary VALUES ("може");
INSERT INTO stop_words_dictionary VALUES ("моє");
INSERT INTO stop_words_dictionary VALUES ("мої");
INSERT INTO stop_words_dictionary VALUES ("мій");
INSERT INTO stop_words_dictionary VALUES ("ми");
INSERT INTO stop_words_dictionary VALUES ("на");
INSERT INTO stop_words_dictionary VALUES ("назавжди");
INSERT INTO stop_words_dictionary VALUES ("над");
INSERT INTO stop_words_dictionary VALUES ("треба");
INSERT INTO stop_words_dictionary VALUES ("нарешті");
INSERT INTO stop_words_dictionary VALUES ("нас");
INSERT INTO stop_words_dictionary VALUES ("наш");
INSERT INTO stop_words_dictionary VALUES ("не");
INSERT INTO stop_words_dictionary VALUES ("ні");
INSERT INTO stop_words_dictionary VALUES ("небудь");
INSERT INTO stop_words_dictionary VALUES ("ніколи");
INSERT INTO stop_words_dictionary VALUES ("нічого");
INSERT INTO stop_words_dictionary VALUES ("але");
INSERT INTO stop_words_dictionary VALUES ("ну");
INSERT INTO stop_words_dictionary VALUES ("про");
INSERT INTO stop_words_dictionary VALUES ("однак");
INSERT INTO stop_words_dictionary VALUES ("він");
INSERT INTO stop_words_dictionary VALUES ("вона");
INSERT INTO stop_words_dictionary VALUES ("вони");
INSERT INTO stop_words_dictionary VALUES ("воно");
INSERT INTO stop_words_dictionary VALUES ("знову");
INSERT INTO stop_words_dictionary VALUES ("від");
INSERT INTO stop_words_dictionary VALUES ("тому");
INSERT INTO stop_words_dictionary VALUES ("дуже");
INSERT INTO stop_words_dictionary VALUES ("перед");
INSERT INTO stop_words_dictionary VALUES ("по");
INSERT INTO stop_words_dictionary VALUES ("під");
INSERT INTO stop_words_dictionary VALUES ("після");
INSERT INTO stop_words_dictionary VALUES ("потім");
INSERT INTO stop_words_dictionary VALUES ("майже");
INSERT INTO stop_words_dictionary VALUES ("при");
INSERT INTO stop_words_dictionary VALUES ("раз");
INSERT INTO stop_words_dictionary VALUES ("хіба");
INSERT INTO stop_words_dictionary VALUES ("свою");
INSERT INTO stop_words_dictionary VALUES ("себе");
INSERT INTO stop_words_dictionary VALUES ("сказати");
INSERT INTO stop_words_dictionary VALUES ("зовсім");
INSERT INTO stop_words_dictionary VALUES ("також");
INSERT INTO stop_words_dictionary VALUES ("такі");
INSERT INTO stop_words_dictionary VALUES ("такий");
INSERT INTO stop_words_dictionary VALUES ("там");
INSERT INTO stop_words_dictionary VALUES ("ті");
INSERT INTO stop_words_dictionary VALUES ("тебе");
INSERT INTO stop_words_dictionary VALUES ("тем");
INSERT INTO stop_words_dictionary VALUES ("тепер");
INSERT INTO stop_words_dictionary VALUES ("те");
INSERT INTO stop_words_dictionary VALUES ("тоді");
INSERT INTO stop_words_dictionary VALUES ("того");
INSERT INTO stop_words_dictionary VALUES ("теж");
INSERT INTO stop_words_dictionary VALUES ("тієї");
INSERT INTO stop_words_dictionary VALUES ("тільки");
INSERT INTO stop_words_dictionary VALUES ("той");
INSERT INTO stop_words_dictionary VALUES ("отут");
INSERT INTO stop_words_dictionary VALUES ("ти");
INSERT INTO stop_words_dictionary VALUES ("уже");
INSERT INTO stop_words_dictionary VALUES ("хоч");
INSERT INTO stop_words_dictionary VALUES ("хоча");
INSERT INTO stop_words_dictionary VALUES ("чого");
INSERT INTO stop_words_dictionary VALUES ("чогось");
INSERT INTO stop_words_dictionary VALUES ("чий");
INSERT INTO stop_words_dictionary VALUES ("чому");
INSERT INTO stop_words_dictionary VALUES ("що");
INSERT INTO stop_words_dictionary VALUES ("щось");
INSERT INTO stop_words_dictionary VALUES ("щоб");
INSERT INTO stop_words_dictionary VALUES ("ледве");
INSERT INTO stop_words_dictionary VALUES ("чиє");
INSERT INTO stop_words_dictionary VALUES ("чия");
INSERT INTO stop_words_dictionary VALUES ("ця");
INSERT INTO stop_words_dictionary VALUES ("ці");
INSERT INTO stop_words_dictionary VALUES ("це");
INSERT INTO stop_words_dictionary VALUES ("цю");
INSERT INTO stop_words_dictionary VALUES ("цього");
INSERT INTO stop_words_dictionary VALUES ("цьому");
INSERT INTO stop_words_dictionary VALUES ("цей");


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
