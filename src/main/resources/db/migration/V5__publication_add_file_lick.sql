ALTER TABLE `publication` ADD COLUMN `file_link` VARCHAR(500) NULL COMMENT '';
ALTER TABLE `publication` ADD CONSTRAINT `unique_page` UNIQUE (`link`, `title`);