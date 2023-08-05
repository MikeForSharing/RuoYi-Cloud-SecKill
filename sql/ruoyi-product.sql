/*
Navicat MySQL Data Transfer

Source Server         : localhostMysql
Source Server Version : 50734
Source Host           : localhost:3306
Source Database       : ruoyi-product

Target Server Type    : MYSQL
Target Server Version : 50734
File Encoding         : 65001

Date: 2023-07-29 15:55:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(200) DEFAULT NULL,
  `product_title` varchar(200) DEFAULT NULL,
  `product_img` varchar(200) DEFAULT NULL,
  `product_detail` longtext,
  `product_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
