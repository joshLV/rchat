ALTER TABLE `t_talkback_number` ADD COLUMN `short_value`  varchar(20);
ALTER TABLE `t_talkback_number` ADD COLUMN `talkback_user_id`  varchar(36) NOT NULL;

ALTER TABLE `t_talkback_number` ADD CONSTRAINT `FKjk68bf80efqsjp3fhumn9rnn7` FOREIGN KEY (`talkback_user_id`) REFERENCES `t_talkback_number` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

