ALTER TABLE `publication` DROP COLUMN `fileNameOriginal`;

ALTER TABLE `publication` ADD COLUMN `fileNameOriginal` VARCHAR(250) NULL COMMENT '';