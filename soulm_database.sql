-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: soulm_database
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `complaint_images`
--

DROP TABLE IF EXISTS `complaint_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaint_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `complaint_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr54r4kd3nsre6gf9hhhqgiwde` (`complaint_id`),
  CONSTRAINT `FKr54r4kd3nsre6gf9hhhqgiwde` FOREIGN KEY (`complaint_id`) REFERENCES `complaints` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaint_images`
--

LOCK TABLES `complaint_images` WRITE;
/*!40000 ALTER TABLE `complaint_images` DISABLE KEYS */;
INSERT INTO `complaint_images` VALUES (1,'https://www.shutterstock.com/shutterstock/photos/2611965065/display_1500/stock-photo-young-caucasian-man-at-outdoors-listening-music-and-singing-2611965065.jpg',2);
/*!40000 ALTER TABLE `complaint_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaints`
--

DROP TABLE IF EXISTS `complaints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaints` (
  `complainant_id` bigint DEFAULT NULL,
  `complained_id` bigint DEFAULT NULL,
  `complaint_date` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdb8c9bai4ixco2yy0xlqqru57` (`complained_id`),
  KEY `FKdslo86yhaf654rli2g96sol2e` (`complainant_id`),
  CONSTRAINT `FKdb8c9bai4ixco2yy0xlqqru57` FOREIGN KEY (`complained_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKdslo86yhaf654rli2g96sol2e` FOREIGN KEY (`complainant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaints`
--

LOCK TABLES `complaints` WRITE;
/*!40000 ALTER TABLE `complaints` DISABLE KEYS */;
INSERT INTO `complaints` VALUES (3,2,'2025-04-10 02:27:06.806815',2,'Spam messages'),(2,3,'2025-04-27 01:12:20.593276',7,'Bu kullanıcı uygunsuz mesajlar gönderdi.'),(7,2,'2025-04-27 01:13:54.698948',8,'Fake Photo'),(7,7,'2025-05-10 02:18:03.877739',9,'Inappropriate Behavior');
/*!40000 ALTER TABLE `complaints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `managers`
--

DROP TABLE IF EXISTS `managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `managers` (
  `birth_date` date DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `managers`
--

LOCK TABLES `managers` WRITE;
/*!40000 ALTER TABLE `managers` DISABLE KEYS */;
INSERT INTO `managers` VALUES ('1990-05-01','2010-06-01',2,'manager2@example.com','http://localhost:8080/uploads/779e2385-1bc5-4b3a-be16-65c4e4648152_a.jpg','$2a$10$5DylO5IrjFmq5Ay6bq4uEuhupIAWSe5acoBqvDqyxnIovG9LM7ftW','+905551112233','manager','hakan');
/*!40000 ALTER TABLE `managers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `matched_at` datetime(6) DEFAULT NULL,
  `user1_id` bigint DEFAULT NULL,
  `user2_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgxaf471cy6rk84ux6avpw5vb0` (`user2_id`),
  KEY `FKow9p2p6lb04rmjphffgyc48y` (`user1_id`),
  CONSTRAINT `FKgxaf471cy6rk84ux6avpw5vb0` FOREIGN KEY (`user2_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKow9p2p6lb04rmjphffgyc48y` FOREIGN KEY (`user1_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matches`
--

LOCK TABLES `matches` WRITE;
/*!40000 ALTER TABLE `matches` DISABLE KEYS */;
INSERT INTO `matches` VALUES (10,NULL,7,11);
/*!40000 ALTER TABLE `matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `is_image` bit(1) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt05r0b6n0iis8u7dfna4xdh73` (`receiver_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt05r0b6n0iis8u7dfna4xdh73` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (_binary '\0',11,3,7,'2025-05-10 01:00:13.717499','merhaba'),(_binary '\0',12,3,7,'2025-05-10 01:00:16.061238','selam'),(_binary '\0',13,3,19,'2025-05-10 01:02:07.320408','dostum'),(_binary '\0',14,3,7,'2025-05-10 01:14:42.650249','.'),(_binary '\0',15,3,7,'2025-05-10 05:14:45.759500','1'),(_binary '\0',20,3,7,'2025-05-10 01:45:39.012103','3'),(_binary '\0',21,3,7,'2025-05-10 01:45:41.133587','4'),(_binary '\0',22,7,3,'2025-05-10 01:49:44.318041','5'),(_binary '\0',23,3,7,'2025-05-10 01:50:10.396203','merhaba'),(_binary '\0',24,2,7,'2025-05-10 02:19:08.803814','dddddd'),(_binary '\0',25,19,7,'2025-05-10 02:19:12.380075','ddddddddddd'),(_binary '\0',26,7,7,'2025-05-10 02:19:16.450186','ddddd'),(_binary '\0',27,7,3,'2025-05-10 02:20:51.831961','dddddd'),(_binary '\0',28,19,7,'2025-05-10 03:00:02.373575','dddd'),(_binary '\0',29,7,11,'2025-05-10 03:03:13.660700','selammm burak'),(_binary '\0',30,11,7,'2025-05-10 03:29:21.938403','hello'),(_binary '\0',31,11,7,'2025-05-10 03:29:25.401977','hi');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `moderators`
--

DROP TABLE IF EXISTS `moderators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `moderators` (
  `birth_date` date DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `moderators`
--

LOCK TABLES `moderators` WRITE;
/*!40000 ALTER TABLE `moderators` DISABLE KEYS */;
INSERT INTO `moderators` VALUES ('1992-03-03','2018-04-03',7,'moderator@example.com','http://localhost:8080/uploads/acbbc460-7a1c-4e25-b130-26b4d196abd5_a.jpg','$2a$10$Hyq22qZKBh2uLs1G.MHFkuJqdCOtu0reBXX1gH6x1xPeVXxbQm5v6','+55333362323','moderator','kerem ss'),('1992-03-04','2018-02-04',9,'moderator2@example.com','http://localhost:8080/uploads/14794b9e-c7ba-481e-9c30-ddea12c87e6a_WhatsApp%20Image%202025-05-09%20at%2017.14.15.jpg','$2a$10$rIwihrM9QAFIor8SyIDfo.K9q0yh68zlGpYGz7maEnGHkwTrmqemC','+55333362322','moderator','Hasan Akınn');
/*!40000 ALTER TABLE `moderators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp_codes`
--

DROP TABLE IF EXISTS `otp_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `expiry` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_codes`
--

LOCK TABLES `otp_codes` WRITE;
/*!40000 ALTER TABLE `otp_codes` DISABLE KEYS */;
INSERT INTO `otp_codes` VALUES (15,'247456','fth@gmail.com','2025-05-08 20:10:59.141094');
/*!40000 ALTER TABLE `otp_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `token` varchar(255) NOT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `type` enum('EMAIL_VERIFICATION','PASSWORD_RESET') DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`token`),
  KEY `FKj8rfw4x0wjjyibfqq566j4qng` (`user_id`),
  CONSTRAINT `FKj8rfw4x0wjjyibfqq566j4qng` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES ('02f089d7-252d-4dbf-9665-d140474009fd','2025-05-06 02:26:19.101623','PASSWORD_RESET',12),('0c160afc-919c-4eaf-bcc3-93e87d750c77','2025-05-06 01:39:41.867140','PASSWORD_RESET',12),('260166be-4184-4f27-b2ad-3164997ed82a','2025-05-06 01:31:30.281093','PASSWORD_RESET',12),('2d28a906-d18c-4431-a8ba-1965f50e309e','2025-05-06 01:34:56.706316','PASSWORD_RESET',12),('30cf825e-bbff-493e-a0a9-ffd7b726c32d','2025-05-06 01:34:53.870586','PASSWORD_RESET',12),('49299861-4d56-4848-abbe-1443d50c4896','2025-05-06 01:50:20.060550','PASSWORD_RESET',12),('4aa95f1d-fe27-48a3-9aee-10c3ce51c4d2','2025-05-06 01:38:44.386995','PASSWORD_RESET',12),('4c595fdc-0443-436d-95e6-e85219f01dcf','2025-05-06 01:38:27.476327','PASSWORD_RESET',12),('5d3830d3-e999-47ff-834d-5a794aa193f5','2025-05-06 01:53:15.339124','PASSWORD_RESET',12),('8096553f-2bee-4e48-8289-c4f115372b1b','2025-05-06 01:42:25.655299','PASSWORD_RESET',12),('996314f6-4e4f-499d-b452-f4fed4e16d9c','2025-05-06 01:58:36.879629','PASSWORD_RESET',12),('d9e4be28-4b57-48ca-992e-b5fc198522b3','2025-05-06 02:06:55.504267','PASSWORD_RESET',12);
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_images`
--

DROP TABLE IF EXISTS `user_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl1lf9kxrd8ybmovqsxcuxhw42` (`user_id`),
  CONSTRAINT `FKl1lf9kxrd8ybmovqsxcuxhw42` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_images`
--

LOCK TABLES `user_images` WRITE;
/*!40000 ALTER TABLE `user_images` DISABLE KEYS */;
INSERT INTO `user_images` VALUES (4,3,'https://www.shutterstock.com/shutterstock/photos/2611965065/display_1500/stock-photo-young-caucasian-man-at-outdoors-listening-music-and-singing-2611965065.jpg'),(5,3,'https://www.shutterstock.com/shutterstock/photos/2583764763/display_1500/stock-photo-portrait-of-handsome-young-man-with-wavy-hair-and-beaming-smile-young-man-with-wavy-hair-and-a-2583764763.jpg'),(6,2,'http://localhost:8080/uploads/3ec03ab7-e312-41dc-a21a-a61782557fb2_WhatsApp%20Image%202025-05-09%20at%2017.14.16.jpg'),(9,11,'http://localhost:8080/uploads/33cc7af2-c1ad-4e11-a1e5-d5c48d69ee07_WhatsApp%20Image%202025-05-09%20at%2017.14.15.jpg'),(10,12,'http://localhost:8080/uploads/3bc02ac2-848e-46aa-9bcd-fa751c0ae2af_WhatsApp%20Image%202025-05-09%20at%2017.14.18.jpg'),(11,19,'https://www.shutterstock.com/shutterstock/photos/2613048695/display_1500/stock-photo-smiling-bearded-man-with-backpack-sitting-on-a-large-rock-at-the-edge-of-a-cliff-enjoying-the-2613048695.jpg'),(52,2,'http://localhost:8080/uploads/128b78de-2433-4c39-868b-24013900c975_WhatsApp%20Image%202025-05-09%20at%2017.14.15.jpg'),(53,7,'http://localhost:8080/uploads/f12020b0-ca1e-4249-97b9-525df0c6ec80_WhatsApp%20Image%202025-05-09%20at%2017.14.16.jpg'),(54,7,'http://localhost:8080/uploads/33cc7af2-c1ad-4e11-a1e5-d5c48d69ee07_WhatsApp%20Image%202025-05-09%20at%2017.14.15.jpg'),(55,7,'http://localhost:8080/uploads/47f1d9f2-60f5-446d-9ca0-0b111a157044_WhatsApp%20Image%202025-05-09%20at%2017.14.14.jpg');
/*!40000 ALTER TABLE `user_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_likes`
--

DROP TABLE IF EXISTS `user_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `liked_at` datetime(6) DEFAULT NULL,
  `liked_id` bigint DEFAULT NULL,
  `liker_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo85dbqtsgh4acxgu08k4b8wbj` (`liker_id`),
  KEY `FKrx3awltkyuhawtbd2aswqaxxw` (`liked_id`),
  CONSTRAINT `FKo85dbqtsgh4acxgu08k4b8wbj` FOREIGN KEY (`liker_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKrx3awltkyuhawtbd2aswqaxxw` FOREIGN KEY (`liked_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_likes`
--

LOCK TABLES `user_likes` WRITE;
/*!40000 ALTER TABLE `user_likes` DISABLE KEYS */;
INSERT INTO `user_likes` VALUES (10,'2025-04-27 02:35:28.991269',3,2),(13,NULL,2,3),(19,NULL,7,2),(23,NULL,7,12),(24,'2025-05-09 21:25:24.108916',7,19),(25,'2025-05-09 21:25:25.000835',11,19),(30,'2025-05-10 03:01:20.544710',11,7),(35,'2025-05-10 03:04:29.076255',7,11),(36,'2025-05-10 03:04:29.300103',11,11),(38,'2025-05-10 03:04:29.732037',2,11),(39,'2025-05-10 03:04:29.978245',3,11),(40,'2025-05-10 03:04:30.229098',12,11);
/*!40000 ALTER TABLE `user_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `approved` bit(1) NOT NULL,
  `banned` bit(1) NOT NULL,
  `birth_date` date DEFAULT NULL,
  `height` double DEFAULT NULL,
  `personality_score` int DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(120) DEFAULT NULL,
  `age_preference` varchar(255) DEFAULT NULL,
  `alcohol` varchar(255) DEFAULT NULL,
  `body_type` varchar(255) DEFAULT NULL,
  `distance_preference` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `relationship_type` varchar(255) DEFAULT NULL,
  `smoke` varchar(255) DEFAULT NULL,
  `shorterbio` longtext,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `diet` varchar(255) DEFAULT NULL,
  `favorite_music` varchar(255) DEFAULT NULL,
  `hobbies` varchar(255) DEFAULT NULL,
  `weekend_plans` varchar(255) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `confirmed` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary '',_binary '\0','1995-05-10',180,20,75,2,'Hakan Ak','25-35','Occasionally','Athletic','50km','krmylmz@gmail.com','Male','İstanbul','$2a$10$wGRNmxTVjNBhAcSQK354QOQp/e5HqiL6bA9JfVh.ZICSTHgSNxNn.','Serious','No','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','krm','a','pop','hiking','walking',41.0638,29.0219,_binary '\0'),(_binary '',_binary '\0','1995-05-10',180,5,75,3,'Kerem Işık','25-35','Occasionally','Athletic','50','ali@example.com','Male','İstanbul','$2a$10$rYnUtofw04CPkTqkg8piveBGVrWOCvsJ5xrd2PK81DZ4lh8N/4ccK','Serious','No','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','ali','a','pop','hiking','walking',41.0638,29.0219,_binary '\0'),(_binary '',_binary '\0','1995-05-10',150,0,50,7,'Burak Bulut','25-35','no','fit','50','user@example.com','Male','28.959100,41.018800','$2a$10$v5xOUAiSK3TFT9osw8ebcetzgOwMfms4he9znQrvBtoxyM0KtFOW.','casual','no','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','user','a','rock','hiking','walking',41.0188,28.9591,_binary '\0'),(_binary '\0',_binary '\0','1990-05-19',180,0,75,11,'Hasan Akın','25-35','Yes','Athletic','50','user2@example.com','Male','İstanbul','$2a$10$3Qwr5hi4onhDVhlaTjEZnOzxwOEg8a7PEMlAXazeLgf8nmWUlzu.a','Serious Relationship','Yes','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','user2','','rock','hiking','walking',41.0188,28.9591,_binary '\0'),(_binary '\0',_binary '\0','2001-01-01',180,0,25,12,'Fatih Demir','25-35','No','Athletic','25','a@b.com','Male','Ankara','$2a$10$f7My4DOSLDSfE362zxu1JOnkM0crqM64qwvrrS7CAuWLyL3u7h/YC','Serious Relationship','No','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','Fatih','','rock','hiking','walking',41.1987,29.2103,_binary '\0'),(_binary '',_binary '\0','2002-02-02',180,0,75,19,'Ayşe Demir','25-35','Yes',NULL,'50','fth@gmail.com','Female','İstanbul','$2a$10$EAhbPrJmKXP0DZOCkSmDoOhpT2d/335tNDQQf2qV3Mtc78H2WMxQa','Casual Dating','Yes','Merhaba! Yeni insanlarla tanışmak için buradayım.','user','userrr','','rock','hiking','walking',41.4685,29.5872,_binary '\0');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-10  4:28:07
