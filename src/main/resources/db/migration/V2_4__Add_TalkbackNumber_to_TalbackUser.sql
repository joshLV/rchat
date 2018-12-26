ALTER TABLE `t_talkback_user`
ADD COLUMN `talkback_number_id`  varchar(36) NOT NULL;

ALTER TABLE `t_talkback_user` ADD CONSTRAINT `FKh3qcqg506lwmjlanrcytrscm6` FOREIGN KEY (`talkback_number_id`) REFERENCES `t_talkback_number` (`id`);

