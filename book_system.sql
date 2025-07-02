/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : book_system

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 28/06/2025 20:34:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL,
  `stock` int(11) NULL DEFAULT 0,
  `category_id` int(11) NULL DEFAULT NULL,
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, 'Java编程思想', 'Bruce Eckel', 107.00, 46, 3, 'http://localhost:8080/api/files/7437f02a-7b52-4dfb-b546-ec267cb659f4.jpg', '《Java编程思想》是Java领域的经典著作，详细介绍了Java语言的方方面面。从面向对象编程的基本概念到高级特性，从集合框架到并发编程，本书为Java程序员提供了全面而深入的指导。作者Bruce Eckel以其独特的见解和丰富的经验，帮助读者真正理解Java编程的精髓。');
INSERT INTO `books` VALUES (2, '红楼梦', '曹雪芹', 59.70, 27, 1, 'https://picsum.photos/300/400?random=2', '《红楼梦》是中国古典四大名著之一，以贾宝玉、林黛玉、薛宝钗的爱情婚姻悲剧为主线，以贾、史、王、薛四大家族的兴衰为背景，反映了封建社会的种种弊端。作品塑造了众多栩栩如生的人物形象，是中国古典小说的巅峰之作。');
INSERT INTO `books` VALUES (3, '高等数学', '同济大学', 56.20, 96, 4, 'https://picsum.photos/300/400?random=3', '《高等数学》是理工科学生的重要基础课程教材。本书由同济大学数学系编写，内容包括函数与极限、导数与微分、积分学、微分方程等核心内容。教材注重数学概念的阐述和数学思维的培养，为学生后续专业课程学习奠定坚实的数学基础。');
INSERT INTO `books` VALUES (4, 'Spring Boot实战', '丁雪丰', 89.00, 25, 3, 'https://picsum.photos/300/400?random=4', '《Spring Boot实战》是学习Spring Boot框架的实用指南。书中通过大量实际案例，详细介绍了Spring Boot的核心特性、自动配置、起步依赖等内容。从项目创建到部署上线，全面覆盖Spring Boot开发的各个环节，是Java Web开发者的必备参考书。');
INSERT INTO `books` VALUES (5, '三体', '刘慈欣', 39.00, 60, 1, 'https://picsum.photos/300/400?random=5', '《三体》是刘慈欣创作的长篇科幻小说。作品描述了地球文明与三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。小说融合了丰富的科学知识和深刻的哲学思考，被誉为中国科幻文学的里程碑作品，获得了雨果奖等多项国际大奖。');

-- ----------------------------
-- Table structure for category_level1
-- ----------------------------
DROP TABLE IF EXISTS `category_level1`;
CREATE TABLE `category_level1`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category_level1
-- ----------------------------
INSERT INTO `category_level1` VALUES (1, '文学');
INSERT INTO `category_level1` VALUES (2, '科技');
INSERT INTO `category_level1` VALUES (3, '教育');

-- ----------------------------
-- Table structure for category_level2
-- ----------------------------
DROP TABLE IF EXISTS `category_level2`;
CREATE TABLE `category_level2`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category_level2
-- ----------------------------
INSERT INTO `category_level2` VALUES (1, '小说', 1);
INSERT INTO `category_level2` VALUES (2, '散文', 1);
INSERT INTO `category_level2` VALUES (3, '编程', 2);
INSERT INTO `category_level2` VALUES (4, '数学', 2);
INSERT INTO `category_level2` VALUES (5, '语言学习', 3);
INSERT INTO `category_level2` VALUES (6, '专业教材', 3);

-- ----------------------------
-- Table structure for order_details
-- ----------------------------
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE `order_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of order_details
-- ----------------------------
INSERT INTO `order_details` VALUES (1, 1, 1, 1, 108.00);
INSERT INTO `order_details` VALUES (2, 1, 2, 1, 59.70);
INSERT INTO `order_details` VALUES (3, 2, 4, 1, 89.00);
INSERT INTO `order_details` VALUES (4, 3, 1, 3, 107.00);
INSERT INTO `order_details` VALUES (5, 3, 3, 3, 56.20);
INSERT INTO `order_details` VALUES (6, 3, 2, 1, 59.70);
INSERT INTO `order_details` VALUES (7, 4, 3, 1, 56.20);
INSERT INTO `order_details` VALUES (8, 5, 1, 1, 107.00);
INSERT INTO `order_details` VALUES (9, 6, 2, 2, 59.70);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int(11) NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `status` enum('PENDING','PAID','SHIPPED','DELIVERED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no_unique`(`order_no`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD202401001', 2, 167.70, 'DELIVERED');
INSERT INTO `orders` VALUES (2, 'ORD202401002', 3, 89.00, 'PAID');
INSERT INTO `orders` VALUES (3, 'ORD20250628163339280', 2, 549.30, 'PENDING');
INSERT INTO `orders` VALUES (4, 'ORD20250628165258342', 2, 56.20, 'PENDING');
INSERT INTO `orders` VALUES (5, 'ORD20250628170715118', 2, 107.00, 'SHIPPED');
INSERT INTO `orders` VALUES (6, 'ORD20250628185614656', 4, 119.40, 'PAID');

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `quantity` int(11) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_book`(`user_id`, `book_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------
INSERT INTO `shopping_cart` VALUES (9, 2, 2, 2);
INSERT INTO `shopping_cart` VALUES (3, 3, 2, 1);
INSERT INTO `shopping_cart` VALUES (10, 2, 3, 1);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('ADMIN','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_unique`(`username`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123456', 'ADMIN');
INSERT INTO `users` VALUES (2, 'user001', '123456', 'USER');
INSERT INTO `users` VALUES (3, 'user002', '123456', 'USER');
INSERT INTO `users` VALUES (4, 'user003', '123456', 'USER');

SET FOREIGN_KEY_CHECKS = 1;
