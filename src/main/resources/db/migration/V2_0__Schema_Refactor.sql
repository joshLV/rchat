/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50715
Source Host           : localhost:3306
Source Database       : rchat_development

Target Server Type    : MYSQL
Target Server Version : 50715
File Encoding         : 65001

Date: 2017-07-11 09:50:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_agent
-- ----------------------------
DROP TABLE IF EXISTS `t_agent`;
CREATE TABLE `t_agent` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `credit_unit_price` double DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `linkman` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(100) DEFAULT NULL,
  `region` varchar(100) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `administrator` varchar(36) DEFAULT NULL,
  `creator` varchar(36) NOT NULL,
  `parent` varchar(36) DEFAULT NULL,
  `level` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKj6uh5bvcy09ymkl5gupcp61ny` (`name`),
  KEY `FKkl0a497jbvf9xehpm4vvm2d1l` (`administrator`),
  KEY `FKeswfwu3l6hty0cppx3yk0dyg8` (`creator`),
  KEY `FKbrfiqexb2ucql0vcqae20wtv0` (`parent`),
  CONSTRAINT `FKbrfiqexb2ucql0vcqae20wtv0` FOREIGN KEY (`parent`) REFERENCES `t_agent` (`id`),
  CONSTRAINT `FKeswfwu3l6hty0cppx3yk0dyg8` FOREIGN KEY (`creator`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FKkl0a497jbvf9xehpm4vvm2d1l` FOREIGN KEY (`administrator`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_agent
-- ----------------------------
INSERT INTO `t_agent` VALUES ('f1d31dfb-4056-482a-bcb6-35d86fed4017', '2017-06-10 17:12:13', '2017-06-10 17:12:17', '0', 'admin@rongchat.com', '王玉琴', '深圳融洽科技有限公司', '0755-26912540', '深圳', '2', 'aa6323f9-85aa-4b20-9372-5c58c86d6a89', 'aa6323f9-85aa-4b20-9372-5c58c86d6a89', null, '0');

-- ----------------------------
-- Table structure for t_agent_parent
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_parent`;
CREATE TABLE `t_agent_parent` (
  `id` varchar(36) NOT NULL,
  `agent_id` varchar(36) NOT NULL,
  `parent_id` varchar(36) NOT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_parent` (`agent_id`,`parent_id`),
  KEY `fk_agent_parent` (`parent_id`),
  CONSTRAINT `fk_agent` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_agent_parent` FOREIGN KEY (`parent_id`) REFERENCES `t_agent` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_agent_parent
-- ----------------------------

-- ----------------------------
-- Table structure for t_authority
-- ----------------------------
DROP TABLE IF EXISTS `t_authority`;
CREATE TABLE `t_authority` (
  `id` varchar(36) NOT NULL,
  `operations` int(11) NOT NULL,
  `resource_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd2carv0fpggbifjt1na5yd074` (`resource_id`),
  KEY `FKipyv00bko48bkhj5va5wavk00` (`role_id`),
  CONSTRAINT `FKd2carv0fpggbifjt1na5yd074` FOREIGN KEY (`resource_id`) REFERENCES `t_resource` (`id`),
  CONSTRAINT `FKipyv00bko48bkhj5va5wavk00` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_authority
-- ----------------------------
INSERT INTO `t_authority` VALUES ('02d0aa12-a69a-4097-8a3a-92d5a2bb1fce', '1', '057adc1a-90cb-4e68-8a37-f856515ba711', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('0abf4bfe-6a17-42b4-a2b5-8c964b95d8b9', '11', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('0d5667ae-332a-4866-9878-8155decbb211', '0', 'eecda0d5-2860-4525-9796-a7c51b318dc2', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('0e5603c0-a978-4dbd-94fa-fdc2588f9ddc', '1111', '6620873f-620a-4798-a63a-5acc926586a2', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('1381b0df-1737-40ef-b37b-a819a7dd8f2d', '0', '26a7d196-6048-433f-92bb-11e186497d1f', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('146b0ca4-416d-450c-bcb3-9ebea3f26c7e', '11', '6620873f-620a-4798-a63a-5acc926586a2', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('14879b3d-4304-4204-bac2-d530d80b377d', '0', '26a7d196-6048-433f-92bb-11e186497d1f', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('153ea2e6-dc54-4b67-89c8-c6bc64322b53', '0', '26a7d196-6048-433f-92bb-11e186497d1f', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('1549276c-b846-453e-87f5-d21d6f41f9df', '1', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('19b09de7-20ab-4efd-9a67-35631ba76cf2', '1111', '26a7d196-6048-433f-92bb-11e186497d1f', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('1de0af3a-f802-46a1-9f8c-55fa0a132043', '0', '26a7d196-6048-433f-92bb-11e186497d1f', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('2029c716-32c3-4809-ba9c-ee9d99955a10', '1111', '53fd2c22-77f5-42a3-a820-348100d19d2d', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('20948d63-d378-45eb-82e8-20d552ddd86a', '1', '102af2de-0976-491b-b673-793049434f4d', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('20d0eb83-4b12-4be6-9c3b-9c0ca8d4ca7a', '0', '20c62709-1730-4b66-a78a-58f8f9b4106a', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('248cc81f-9899-460d-9108-738b6f77892e', '1111', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('2bf207f0-ceb4-47c2-8f9b-a087b2586e50', '1111', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('2ce0ebb7-169a-4fb9-b1c3-e8bbf9c1a29c', '1', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('2ce74d29-d86c-48cb-957a-b15cfe9c836d', '1111', '057adc1a-90cb-4e68-8a37-f856515ba711', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('2cf3200c-a0e4-4c1e-afb9-84ebd6af4a3b', '0', '53fd2c22-77f5-42a3-a820-348100d19d2d', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('2f387659-97c0-41ce-899f-e51d02acefff', '0', '530d2e1b-2f1d-4d47-9762-7268334210b6', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('38e35559-4a98-43d7-954c-7494a806f9c2', '0', '53fd2c22-77f5-42a3-a820-348100d19d2d', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('39ea397f-ddd0-4eaf-8b63-a1aad46e7e21', '11', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('3af41ee0-2402-4622-bbd6-e8f698c5e612', '1', '36b82a14-a922-4380-a35d-da4a915b697e', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('3b159312-2bc6-4e0f-9b2d-8fda7e24eae3', '0', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('3f416e88-6837-4eeb-90d7-d7aa13a44c51', '1', '20c62709-1730-4b66-a78a-58f8f9b4106a', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('47af6548-cfc6-4d15-a2dc-fe9b3be94aaf', '1', '20c62709-1730-4b66-a78a-58f8f9b4106a', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('4b0250bb-c851-4e97-bcfc-30c65ac1a0db', '1111', '530d2e1b-2f1d-4d47-9762-7268334210b6', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('4c4d7ed2-e0cc-4a6b-acfb-edd4aea6c6ad', '1', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('4df4bdab-bad6-4752-9bca-f0a9f6d325ad', '1', '102af2de-0976-491b-b673-793049434f4d', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('4dff6b31-c430-4554-a804-07b2099a57bb', '11', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('4ff735d3-2b5e-439f-9ad4-8b5fc7451ec3', '1011', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('51626054-b1f2-4672-bc11-1320b3178763', '0', '93cb8548-b9f1-4236-8b56-17166bf7acbd', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('52bfa431-5357-4afc-baf2-118fa67e9e01', '0', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('5527dcb5-9ad1-4427-bf51-02f43f92e73b', '1111', '26a7d196-6048-433f-92bb-11e186497d1f', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('586c93fa-b48b-400e-8933-ae4e624bfa35', '1111', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('5ae86a9a-26c8-4d0a-b79d-e2ff311c89b5', '1', 'eecda0d5-2860-4525-9796-a7c51b318dc2', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('5f654534-9f7b-4bd2-8bf8-2e54d5b8a159', '0', '530d2e1b-2f1d-4d47-9762-7268334210b6', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('68519ae7-63ae-47b5-87a8-1c47e36d68f6', '1111', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('7423fd1a-e283-492a-b199-ad49940d3b46', '0', '6620873f-620a-4798-a63a-5acc926586a2', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('776fa256-f05a-438f-b330-6be53a12d71c', '0', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('7c55eeb6-905b-48bb-9780-a504550c4aea', '1', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('7d3396a9-a7c6-4555-b324-5eab15317795', '0', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('7d81bf5a-17bd-4b0b-a9a1-8a4137db6eba', '1111', '93cb8548-b9f1-4236-8b56-17166bf7acbd', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('7f92ba2a-b7bf-41b9-8ed4-6fc9e1b4e739', '1011', '53fd2c22-77f5-42a3-a820-348100d19d2d', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('8245f255-3c74-4e24-8c1a-8849983f5c91', '1111', '93cb8548-b9f1-4236-8b56-17166bf7acbd', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('83a0d169-82d4-4849-be62-0734b019630f', '1', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('83ba0f07-0fc4-48e9-aead-806c840e2fc1', '1', '36b82a14-a922-4380-a35d-da4a915b697e', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('86772f75-8e9d-4d8a-be8b-779da42e0ceb', '11', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('86916115-7823-43ec-9634-4c9c9bdede2b', '0', '93cb8548-b9f1-4236-8b56-17166bf7acbd', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('88ebc3c7-8f9b-4e87-9f14-8321e78200db', '1', 'eecda0d5-2860-4525-9796-a7c51b318dc2', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('8b268fcc-dee5-400b-b1b7-ad0f10ea5b3c', '1111', '530d2e1b-2f1d-4d47-9762-7268334210b6', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('8bbaf4d3-a8fd-481c-ae3f-e840ed414d57', '1', '36b82a14-a922-4380-a35d-da4a915b697e', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('92ecc92c-0b35-4405-ae15-6a27170bca60', '1', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('943eab58-e4ce-4d73-b491-2c05b1f19b87', '0', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('97689d5e-0477-4e77-906b-78828c1e9ed3', '1', '36b82a14-a922-4380-a35d-da4a915b697e', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('9de97aa4-42d9-4d10-8793-40eeeae5d828', '1111', '057adc1a-90cb-4e68-8a37-f856515ba711', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('9f173c0d-7bc9-4af9-8226-5923133146ec', '0', '6620873f-620a-4798-a63a-5acc926586a2', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('9f2a345c-2c83-45d4-8775-f26d3abe3cf3', '0', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('a08e609b-e945-4cdc-a854-50d43816dc04', '1', '36b82a14-a922-4380-a35d-da4a915b697e', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('a4c396b0-6c72-429c-9b74-631ec3e1ed67', '0', '93cb8548-b9f1-4236-8b56-17166bf7acbd', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('a6a85deb-481b-467d-93e6-84c088f22a61', '1', '102af2de-0976-491b-b673-793049434f4d', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('a9a95697-1931-4c55-baa4-b7fe03c9021a', '1', 'd3dc9448-e830-4488-9c1a-2e5edb31a0b7', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('aebf07bc-da4e-4aac-83a2-e7aa0f4e7775', '1011', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('b1959e5a-87f0-4a07-8956-48f4f211e6be', '1011', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('b81087c2-26e0-4fba-801d-3c1abc8d5f87', '11', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('b95c717e-b974-4bd4-b5d9-720695c8d818', '1', '102af2de-0976-491b-b673-793049434f4d', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('ba55ebcc-d92a-4115-ae25-84dadb635404', '1', '20c62709-1730-4b66-a78a-58f8f9b4106a', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('c5a90b55-f479-4a4e-959c-2908c0936021', '0', '102af2de-0976-491b-b673-793049434f4d', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('c731fcf7-11d1-4f53-9ede-6f6970e4f3f7', '1', '102af2de-0976-491b-b673-793049434f4d', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('c8dfeadc-81a9-463e-86d6-422265c111cf', '1', 'eecda0d5-2860-4525-9796-a7c51b318dc2', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('cef6ba4b-c37f-4abd-a4d7-7e75b21ec9ea', '0', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('d15bc0b7-4c00-4de4-9796-b0539a9a5e66', '0', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('d2338c84-fa07-4503-a5c9-d779f1e3c19e', '1', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('d3debf88-9b66-4284-9382-38d1da01b4db', '0', '6620873f-620a-4798-a63a-5acc926586a2', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('d5afac9c-4849-4dc6-96b5-9e398b6ed2b9', '1111', '530d2e1b-2f1d-4d47-9762-7268334210b6', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('db685894-87fb-4f37-be1d-e5941c2e5751', '11', '2e43ebbb-fc06-4dc6-944b-40b336c9141e', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('dbc84a97-876a-4c26-a0b1-24b758a3518b', '1111', 'fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('dde9cf3e-b48d-4f5b-8f16-e0858400de48', '1111', '6620873f-620a-4798-a63a-5acc926586a2', 'e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0');
INSERT INTO `t_authority` VALUES ('de263eda-934d-4f57-b50a-c44d845e08f6', '0', '93cb8548-b9f1-4236-8b56-17166bf7acbd', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('deae7429-dd4a-44ed-8a71-23db88a59b66', '0', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('e1be57d0-2dfc-47b7-9b8d-3864796523bc', '1', 'ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('e2f3a135-92f1-4965-8f94-00e56028f85b', '1011', '53fd2c22-77f5-42a3-a820-348100d19d2d', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('e4ee759b-e33a-4fab-bb30-a13291154b7f', '1', '20c62709-1730-4b66-a78a-58f8f9b4106a', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('e4f217d5-2ebc-4904-9673-1d223694075e', '1', 'eecda0d5-2860-4525-9796-a7c51b318dc2', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('e73dbfa3-9cc0-4f14-8278-2defb21e6559', '1011', '53fd2c22-77f5-42a3-a820-348100d19d2d', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('e7d42ffd-3e12-4cbd-a26f-102288ef6910', '1111', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('e8ef3e78-3669-49bd-a77c-6db035b38c81', '11', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('eafe4ecd-40c1-4672-92d2-3e921806b43d', '1111', '057adc1a-90cb-4e68-8a37-f856515ba711', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('ec1070fa-61e9-4ce6-b569-824e4603b872', '0', 'fca3a1c5-f729-4751-a389-bbebadcf74b8', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('f254c825-36c2-4c10-8a38-10bc930b9089', '0', '057adc1a-90cb-4e68-8a37-f856515ba711', '8f91fe6b-e1ad-43ea-9827-664ba6af83e7');
INSERT INTO `t_authority` VALUES ('f4d95ffa-8a0b-4eb5-8aea-6fb050c4677f', '1', '20c62709-1730-4b66-a78a-58f8f9b4106a', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('f6bd0284-5779-4cf8-842a-9be01cd768a8', '1', 'eecda0d5-2860-4525-9796-a7c51b318dc2', 'a07bfa07-54c4-482d-9028-74cc9064edbc');
INSERT INTO `t_authority` VALUES ('f6c75647-b34f-4159-811b-d6c79e4e9cf4', '0', '530d2e1b-2f1d-4d47-9762-7268334210b6', 'd3a9f6ab-fd16-4916-bb69-cbc4e202a8ae');
INSERT INTO `t_authority` VALUES ('f935838e-5515-498d-8208-de7db21cd04c', '1', '057adc1a-90cb-4e68-8a37-f856515ba711', '219e8e4d-f32b-4434-b045-734eb9e9f80a');
INSERT INTO `t_authority` VALUES ('fa375bc0-9c08-448e-87ad-75257c05be56', '0', 'fa9ac232-2a52-43ec-9c74-d05e98a720d9', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');
INSERT INTO `t_authority` VALUES ('ffa0c6f4-2c29-46ae-bfaa-df999cdb2bef', '1', '36b82a14-a922-4380-a35d-da4a915b697e', '96cadace-4b09-4a7d-8fe1-a080f94ebbc6');

-- ----------------------------
-- Table structure for t_business
-- ----------------------------
DROP TABLE IF EXISTS `t_business`;
CREATE TABLE `t_business` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `code` varchar(50) NOT NULL,
  `credit_per_month` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_31x4b94w8pivmu050fpx88vsb` (`code`),
  UNIQUE KEY `UK_22g9mcoi7dwonnjynrftidnmx` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_business
-- ----------------------------

-- ----------------------------
-- Table structure for t_business_rent
-- ----------------------------
DROP TABLE IF EXISTS `t_business_rent`;
CREATE TABLE `t_business_rent` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `deadline` datetime NOT NULL,
  `business_id` varchar(36) NOT NULL,
  `talkback_user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5kqea8tt2s6fuq1x6uvjxbj6q` (`business_id`),
  KEY `FKtf5s9nwhcsi6koa20aw6o96eo` (`talkback_user_id`),
  CONSTRAINT `FK5kqea8tt2s6fuq1x6uvjxbj6q` FOREIGN KEY (`business_id`) REFERENCES `t_business` (`id`),
  CONSTRAINT `FKtf5s9nwhcsi6koa20aw6o96eo` FOREIGN KEY (`talkback_user_id`) REFERENCES `t_talkback_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_business_rent
-- ----------------------------

-- ----------------------------
-- Table structure for t_credit_account
-- ----------------------------
DROP TABLE IF EXISTS `t_credit_account`;
CREATE TABLE `t_credit_account` (
  `disc` varchar(10) NOT NULL,
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `credit` bigint(20) DEFAULT '0',
  `group_id` varchar(36) DEFAULT NULL,
  `agent_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj6h5qevr9ib9ilfgg6or2mbdc` (`group_id`),
  KEY `FK85brdn98iifu01e6nim1dpsem` (`agent_id`),
  CONSTRAINT `FK85brdn98iifu01e6nim1dpsem` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FKj6h5qevr9ib9ilfgg6or2mbdc` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_credit_account
-- ----------------------------
INSERT INTO `t_credit_account` VALUES ('group', '52c55d5b-a73e-4f41-8e97-7e54147a8cc4', '2017-06-10 17:17:58', '2017-06-10 17:18:00', '99999999', 'a1d142be-d49b-462c-bd73-60cee464f146', null);
INSERT INTO `t_credit_account` VALUES ('agent', 'f23fc9c0-3024-40a1-8d6e-f8003ae878e8', '2017-06-10 17:17:20', '2017-06-10 17:17:23', '99999999', null, 'f1d31dfb-4056-482a-bcb6-35d86fed4017');

-- ----------------------------
-- Table structure for t_credit_order
-- ----------------------------
DROP TABLE IF EXISTS `t_credit_order`;
CREATE TABLE `t_credit_order` (
  `disc` varchar(20) NOT NULL,
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `deadline` datetime NOT NULL,
  `distributed_credit` bigint(20) DEFAULT '0',
  `remanent_credit` bigint(20) DEFAULT '0',
  `renew_time` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `respondent_id` varchar(36) NOT NULL,
  `group_id` varchar(36) DEFAULT NULL,
  `agent_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr4rkf3qcdfbbry5r6vwpsgeb` (`respondent_id`),
  KEY `FK36395b03kdi40di7hn56hyg2v` (`group_id`),
  KEY `FKlk4bmjw3l2t9sc69kwhuwk38j` (`agent_id`),
  CONSTRAINT `FK36395b03kdi40di7hn56hyg2v` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FKlk4bmjw3l2t9sc69kwhuwk38j` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FKr4rkf3qcdfbbry5r6vwpsgeb` FOREIGN KEY (`respondent_id`) REFERENCES `t_agent` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_credit_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `linkman` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(100) DEFAULT NULL,
  `gps_enabled` bit(1) DEFAULT NULL,
  `over_group_enabled` bit(1) DEFAULT NULL,
  `over_level_call_type` int(11) DEFAULT NULL,
  `administrator` varchar(36) NOT NULL,
  `creator` varchar(36) DEFAULT NULL,
  `group_id` varchar(36) NOT NULL,
  `parent` varchar(36) DEFAULT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK45rdwvewadfgew6y4o0ulf9wn` (`administrator`),
  KEY `FK4b183st3f22871xc7ndo595wr` (`creator`),
  KEY `FK6x3fo7g35ghw16ormdebu2x62` (`group_id`),
  KEY `FK8ymm4qgsbgelnc0ivpfgim4x7` (`parent`),
  CONSTRAINT `FK45rdwvewadfgew6y4o0ulf9wn` FOREIGN KEY (`administrator`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK4b183st3f22871xc7ndo595wr` FOREIGN KEY (`creator`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK6x3fo7g35ghw16ormdebu2x62` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`),
  CONSTRAINT `FK8ymm4qgsbgelnc0ivpfgim4x7` FOREIGN KEY (`parent`) REFERENCES `t_department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_department
-- ----------------------------

-- ----------------------------
-- Table structure for t_department_parent
-- ----------------------------
DROP TABLE IF EXISTS `t_department_parent`;
CREATE TABLE `t_department_parent` (
  `id` varchar(36) NOT NULL,
  `department_id` varchar(36) NOT NULL,
  `parent_id` varchar(36) DEFAULT NULL,
  `level` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department_parent` (`department_id`,`parent_id`),
  KEY `fk_department_parent` (`parent_id`),
  CONSTRAINT `fk_department` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_department_parent` FOREIGN KEY (`parent_id`) REFERENCES `t_department` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_department_parent
-- ----------------------------

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `credit_unit_price` double DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `linkman` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(100) DEFAULT NULL,
  `scale` int(11) DEFAULT NULL,
  `vip` bit(1) NOT NULL,
  `administrator` varchar(36) DEFAULT NULL,
  `agent_id` varchar(36) NOT NULL,
  `creator` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsw96rg1m1r6q8cp787dx7ugru` (`name`),
  KEY `FKodp450bhwatk3h4n54vkbue6x` (`administrator`),
  KEY `FKmnvq2ma0aslx71x7ym463muls` (`agent_id`),
  KEY `FKe4bn6dd04yugxdbjc7excnxq3` (`creator`),
  CONSTRAINT `FKe4bn6dd04yugxdbjc7excnxq3` FOREIGN KEY (`creator`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FKmnvq2ma0aslx71x7ym463muls` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`),
  CONSTRAINT `FKodp450bhwatk3h4n54vkbue6x` FOREIGN KEY (`administrator`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_group
-- ----------------------------
INSERT INTO `t_group` VALUES ('a1d142be-d49b-462c-bd73-60cee464f146', '2017-06-10 17:13:39', '2017-06-10 17:13:41', '0', '融洽', 'admin@rongchat.com', '王玉琴', '深圳融洽科技有限公司', '0755-26912540', '2', '', 'aa6323f9-85aa-4b20-9372-5c58c86d6a89', 'f1d31dfb-4056-482a-bcb6-35d86fed4017', 'aa6323f9-85aa-4b20-9372-5c58c86d6a89');

-- ----------------------------
-- Table structure for t_group_business
-- ----------------------------
DROP TABLE IF EXISTS `t_group_business`;
CREATE TABLE `t_group_business` (
  `group_id` varchar(36) NOT NULL,
  `business_id` varchar(36) NOT NULL,
  UNIQUE KEY `UK17xxiywi66ksw0hmq7lxfin1b` (`group_id`,`business_id`),
  KEY `FKbs03b7t966jom2bcmyru64230` (`business_id`),
  CONSTRAINT `FK4ypmaton3wkl8ua4ixsk2dq63` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKbs03b7t966jom2bcmyru64230` FOREIGN KEY (`business_id`) REFERENCES `t_business` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_group_business
-- ----------------------------

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `message` text,
  `operation` varchar(255) DEFAULT NULL,
  `resource` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `user_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbvn5yabu3vqwvtjoh32i9r4ip` (`user_id`),
  CONSTRAINT `FKbvn5yabu3vqwvtjoh32i9r4ip` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log
-- ----------------------------

-- ----------------------------
-- Table structure for t_log_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_log_detail`;
CREATE TABLE `t_log_detail` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `resource_id` varchar(255) DEFAULT NULL,
  `resource_name` varchar(255) DEFAULT NULL,
  `step` int(11) NOT NULL,
  `table_name` varchar(255) DEFAULT NULL,
  `log_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK21slkae2l0cmihkf4t7g3gsi2` (`log_id`),
  CONSTRAINT `FK21slkae2l0cmihkf4t7g3gsi2` FOREIGN KEY (`log_id`) REFERENCES `t_log` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log_detail
-- ----------------------------

-- ----------------------------
-- Table structure for t_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource` (
  `id` varchar(36) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `resource_group_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1ydbu8p94uwndrb4i05dhbj8g` (`resource_group_id`),
  CONSTRAINT `FK1ydbu8p94uwndrb4i05dhbj8g` FOREIGN KEY (`resource_group_id`) REFERENCES `t_resource_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_resource
-- ----------------------------
INSERT INTO `t_resource` VALUES ('057adc1a-90cb-4e68-8a37-f856515ba711', '对讲号段', 'segment', null);
INSERT INTO `t_resource` VALUES ('102af2de-0976-491b-b673-793049434f4d', '业务应用', 'service', null);
INSERT INTO `t_resource` VALUES ('20c62709-1730-4b66-a78a-58f8f9b4106a', '资源', 'resource', null);
INSERT INTO `t_resource` VALUES ('26a7d196-6048-433f-92bb-11e186497d1f', '代理商', 'agent', null);
INSERT INTO `t_resource` VALUES ('2e43ebbb-fc06-4dc6-944b-40b336c9141e', '集团', 'group', null);
INSERT INTO `t_resource` VALUES ('36b82a14-a922-4380-a35d-da4a915b697e', '角色', 'role', null);
INSERT INTO `t_resource` VALUES ('530d2e1b-2f1d-4d47-9762-7268334210b6', '部门', 'department', null);
INSERT INTO `t_resource` VALUES ('53fd2c22-77f5-42a3-a820-348100d19d2d', '额度订单', 'credit_order', null);
INSERT INTO `t_resource` VALUES ('6620873f-620a-4798-a63a-5acc926586a2', '对讲账号', 'talkback_number', null);
INSERT INTO `t_resource` VALUES ('93cb8548-b9f1-4236-8b56-17166bf7acbd', '公司', 'company', null);
INSERT INTO `t_resource` VALUES ('ad38d0cd-8d1b-4a6f-be19-107a87d62b5b', '用户', 'user', null);
INSERT INTO `t_resource` VALUES ('d3dc9448-e830-4488-9c1a-2e5edb31a0b7', '资源组', 'resource_group', null);
INSERT INTO `t_resource` VALUES ('eecda0d5-2860-4525-9796-a7c51b318dc2', '授权', 'authority', null);
INSERT INTO `t_resource` VALUES ('fa2ebb4b-d3fc-4130-87ea-59d832bcbb76', '额度账户', 'credit_account', null);
INSERT INTO `t_resource` VALUES ('fa9ac232-2a52-43ec-9c74-d05e98a720d9', '终端代理商', 'terminal_agent', null);
INSERT INTO `t_resource` VALUES ('fca3a1c5-f729-4751-a389-bbebadcf74b8', '对讲群组', 'talback_group', null);

-- ----------------------------
-- Table structure for t_resource_group
-- ----------------------------
DROP TABLE IF EXISTS `t_resource_group`;
CREATE TABLE `t_resource_group` (
  `id` varchar(36) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_resource_group
-- ----------------------------

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` varchar(36) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('219e8e4d-f32b-4434-b045-734eb9e9f80a', '部门管理员', 'department_admin');
INSERT INTO `t_role` VALUES ('32b62a97-502f-4164-ab7d-1f3c50bad1e0', '平台管理员', 'rchat_admin');
INSERT INTO `t_role` VALUES ('8f91fe6b-e1ad-43ea-9827-664ba6af83e7', '平台额度管理员', 'credit_admin');
INSERT INTO `t_role` VALUES ('96cadace-4b09-4a7d-8fe1-a080f94ebbc6', '普通代理商管理员', 'normal_agent_admin');
INSERT INTO `t_role` VALUES ('a07bfa07-54c4-482d-9028-74cc9064edbc', '集团管理员', 'group_admin');
INSERT INTO `t_role` VALUES ('d3a9f6ab-fd16-4916-bb69-cbc4e202a8ae', '终端代理商管理员', 'terminal_agent_admin');
INSERT INTO `t_role` VALUES ('e0b3e2f9-bb76-4872-96b0-34bf12c2a3a0', '公司管理员', 'company_group');

-- ----------------------------
-- Table structure for t_segment
-- ----------------------------
DROP TABLE IF EXISTS `t_segment`;
CREATE TABLE `t_segment` (
  `disc` varchar(31) NOT NULL,
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `value` int(11) NOT NULL,
  `internal` bit(1) NOT NULL,
  `vip` bit(1) NOT NULL DEFAULT b'0',
  `agent_id` varchar(36) DEFAULT NULL,
  `agent_segment_id` varchar(36) DEFAULT NULL,
  `group_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfsxqdgyqt377gc4yrxnc45ppv` (`agent_id`,`value`),
  UNIQUE KEY `UKfsgdehigwqkkd8tyvo3x1xoew` (`group_id`,`value`,`agent_segment_id`) USING BTREE,
  KEY `FK8h5dkyha1a52kcprklpcbww90` (`agent_segment_id`),
  CONSTRAINT `FK8h5dkyha1a52kcprklpcbww90` FOREIGN KEY (`agent_segment_id`) REFERENCES `t_segment` (`id`),
  CONSTRAINT `FKd6a5xcg9jum0jgejk70ugqv22` FOREIGN KEY (`agent_id`) REFERENCES `t_agent` (`id`),
  CONSTRAINT `FKrg97otskrneomyn683avbmwq0` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_segment
-- ----------------------------
INSERT INTO `t_segment` VALUES ('group', '56478d5c-3f2c-4ccf-afdc-0f39d087fcf2', '2017-06-26 10:32:21', '2017-07-05 23:00:08', '0', '\0', '', null, '8e775c01-526d-448c-86b3-b358370f3425', 'a1d142be-d49b-462c-bd73-60cee464f146');
INSERT INTO `t_segment` VALUES ('agent', '8e775c01-526d-448c-86b3-b358370f3425', '2017-06-26 10:31:33', '2017-06-26 10:31:36', '0', '\0', '', 'f1d31dfb-4056-482a-bcb6-35d86fed4017', null, null);

-- ----------------------------
-- Table structure for t_talkback_group
-- ----------------------------
DROP TABLE IF EXISTS `t_talkback_group`;
CREATE TABLE `t_talkback_group` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `department_id` varchar(36) DEFAULT NULL,
  `group_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKndpv2eji4tsjkp8im1g24qmls` (`department_id`),
  KEY `FKru5i14k90oat4f4m3m3rj0cl` (`group_id`),
  CONSTRAINT `FKndpv2eji4tsjkp8im1g24qmls` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`),
  CONSTRAINT `FKru5i14k90oat4f4m3m3rj0cl` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_talkback_group
-- ----------------------------

-- ----------------------------
-- Table structure for t_talkback_number
-- ----------------------------
DROP TABLE IF EXISTS `t_talkback_number`;
CREATE TABLE `t_talkback_number` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `value` int(11) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `group_segment_id` varchar(36) DEFAULT NULL,
  `full_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKktnsjg6wpjcxc97dsafxmwinh` (`group_segment_id`),
  CONSTRAINT `FKktnsjg6wpjcxc97dsafxmwinh` FOREIGN KEY (`group_segment_id`) REFERENCES `t_segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_talkback_number
-- ----------------------------

-- ----------------------------
-- Table structure for t_talkback_user
-- ----------------------------
DROP TABLE IF EXISTS `t_talkback_user`;
CREATE TABLE `t_talkback_user` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `activated` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `call_in_permissions` varchar(255) DEFAULT NULL,
  `call_out_permissions` varchar(255) DEFAULT NULL,
  `gps_enabled` bit(1) DEFAULT NULL,
  `gps_interval` int(11) DEFAULT NULL,
  `representative` bit(1) NOT NULL,
  `role` int(11) NOT NULL,
  `department_id` varchar(36) DEFAULT NULL,
  `group_id` varchar(36) NOT NULL,
  `talback_number_id` varchar(36) NOT NULL,
  `last_renewed` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1uwtqe1nwykov22rl25uoace4` (`department_id`),
  KEY `FKlt740bcytdgm8uv0y3l4nwwr9` (`group_id`),
  KEY `FKh3qcqg506lwmjlanrcytrscm6` (`talback_number_id`),
  CONSTRAINT `FK1uwtqe1nwykov22rl25uoace4` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`),
  CONSTRAINT `FKh3qcqg506lwmjlanrcytrscm6` FOREIGN KEY (`talback_number_id`) REFERENCES `t_talkback_number` (`id`),
  CONSTRAINT `FKlt740bcytdgm8uv0y3l4nwwr9` FOREIGN KEY (`group_id`) REFERENCES `t_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_talkback_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_talkback_user_group
-- ----------------------------
DROP TABLE IF EXISTS `t_talkback_user_group`;
CREATE TABLE `t_talkback_user_group` (
  `talkback_group_id` varchar(36) NOT NULL,
  `talkback_user_id` varchar(36) NOT NULL,
  PRIMARY KEY (`talkback_group_id`,`talkback_user_id`),
  KEY `FKaryuh3xlvin4ikluclaps1180` (`talkback_user_id`),
  CONSTRAINT `FKaryuh3xlvin4ikluclaps1180` FOREIGN KEY (`talkback_user_id`) REFERENCES `t_talkback_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FKl11b9pmwayxaukrwqa2lprgrm` FOREIGN KEY (`talkback_group_id`) REFERENCES `t_talkback_group` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_talkback_user_group
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` varchar(36) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `activated` bit(1) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `expired` bit(1) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `online` bit(1) NOT NULL,
  `password` varchar(50) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `salt` varchar(50) NOT NULL,
  `username` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjhib4legehrm4yscx9t3lirqi` (`username`),
  KEY `IDXi6qjjoe560mee5ajdg7v1o6mi` (`email`),
  KEY `IDXg8gqk4e142wekcb1t6d3v2mwx` (`name`),
  KEY `IDXm5bu5erj83eubjsa1nyms0t89` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('aa6323f9-85aa-4b20-9372-5c58c86d6a89', '2017-04-08 11:49:46', '2017-07-11 09:31:34', '', 'admin@rongchat.com', '', '\0', '融洽', '\0', '56be2f876624a6e97f4cb05dbc26338f', '400-859-7755', 'zHrDcoHKWvNdm6kP0kAxRE8grP4G3VZb', 'rchat');


-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  UNIQUE KEY `UKpi7i8cjrb75qox4d7wfyfjjpb` (`user_id`,`role_id`),
  KEY `FKa9c8iiy6ut0gnx491fqx4pxam` (`role_id`),
  CONSTRAINT `FKa9c8iiy6ut0gnx491fqx4pxam` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FKq5un6x7ecoef5w1n39cop66kl` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('aa6323f9-85aa-4b20-9372-5c58c86d6a89', '32b62a97-502f-4164-ab7d-1f3c50bad1e0');

-- ----------------------------
-- View structure for v_agent
-- ----------------------------
DROP VIEW IF EXISTS `v_agent`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER  VIEW `v_agent` AS SELECT
    t_agent.id,
    t_agent.created_at,
    t_agent.updated_at,
    t_agent.credit_unit_price,
    t_agent.email,
    t_agent.linkman,
    t_agent.`name`,
    t_agent.phone,
    t_agent.region,
    t_agent.type,
    t_agent.administrator,
    t_agent.creator,
    t_agent.`level`,
    t_agent_parent.parent_id
FROM
    t_agent_parent
INNER JOIN t_agent ON t_agent_parent.agent_id = t_agent.id
ORDER BY
    t_agent_parent.parent_id ASC,
    t_agent.`level` ASC ;

-- ----------------------------
-- View structure for v_department_view
-- ----------------------------
DROP VIEW IF EXISTS `v_department`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER  VIEW `v_department` AS SELECT
    t_department.id,
    t_department.created_at,
    t_department.updated_at,
    t_department.email,
    t_department.linkman,
    t_department.`name`,
    t_department.phone,
    t_department.gps_enabled,
    t_department.over_group_enabled,
    t_department.over_level_call_type,
    t_department.administrator,
    t_department.creator,
    t_department.group_id,
    t_department.`level`,
    t_department_parent.parent_id
FROM
    t_department_parent
INNER JOIN t_department ON t_department_parent.department_id = t_department.id
ORDER BY
    t_department_parent.parent_id ASC,
    t_department.`level` ASC ;

