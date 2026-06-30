-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: supplichainmgt
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `drivers`
--

DROP TABLE IF EXISTS `drivers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `driver_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `nid_number` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `rating` double DEFAULT NULL,
  `total_deliveries` int DEFAULT NULL,
  `total_earnings` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `police_station_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKre66mdta4hy6pxm2w1rqu08jv` (`email`),
  UNIQUE KEY `UKojm6yjeacqc5cthc73k5twsnj` (`user_id`),
  KEY `FK65oo4egjyxf1dq396t4bm228b` (`police_station_id`),
  CONSTRAINT `FK65oo4egjyxf1dq396t4bm228b` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`),
  CONSTRAINT `FKfscpnjt46gco44xh86l99rxh7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drivers`
--

LOCK TABLES `drivers` WRITE;
/*!40000 ALTER TABLE `drivers` DISABLE KEYS */;
INSERT INTO `drivers` VALUES (1,_binary '','উত্তরা সেক্টর ১০, ঢাকা, বাংলাদেশ','2026-06-28 17:32:43.938326','1995-04-12','Md. Rafiqul Islam','rafiq.driver2@gmail.com','MALE','Md._Rafiqul_Islam_1d6cadcb-03c9-40f9-b4dd-ac576ce61d18.JPG','199526912356','01812345688',4.5,120,45000,'2026-06-28 17:32:43.938326','DHAKA-METRO-TA-11-2233','COVERED_VAN',1,NULL),(2,_binary '','উত্তরা সেক্টর ১০, ঢাকা, বাংলাদেশ','2026-06-28 17:42:27.646622','1995-04-12','Md. Islam','rafiq.driver23@gmail.com','MALE','Md._Islam_4ffb7c36-b832-45a1-a4d2-b843d72844a7.JPG','199526912346','01812345658',4.5,120,45000,'2026-06-28 17:42:27.646622','DHAKA-METRO-TA-11-2233','COVERED_VAN',3,NULL),(3,_binary '','উত্তরা সেক্টর ১০, ঢাকা, বাংলাদেশ','2026-06-28 17:44:14.403887','1995-04-12','Md. Islam','rafiq.driver233@gmail.com','MALE','Md._Islam_d4a25525-f351-4281-885f-644511c90742.JPG','1995269123446','01812345558',4.5,120,45000,'2026-06-28 17:44:14.403887','DHAKA-METRO-TA-11-2233','COVERED_VAN',5,NULL),(4,_binary '','House 12, Block C, Mirpur 10, Dhaka','2026-06-30 16:33:24.717287','1999-10-10','Tanvir islam','tanvirhasan0@gmail.com','MALE','uploads/driver/Tanvir_islam_d99dae2d-91b4-4eb1-a2de-dccea072a2ae.JPG','1999261025888','017123456728',0,0,0,'2026-06-30 16:40:55.243046','Dhaka-Metro-TA-11-2222','TRUCK_10_TON',18,4);
/*!40000 ALTER TABLE `drivers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 23:57:47
