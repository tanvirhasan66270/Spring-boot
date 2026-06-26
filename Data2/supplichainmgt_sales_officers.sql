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
-- Table structure for table `sales_officers`
--

DROP TABLE IF EXISTS `sales_officers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_officers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `created_at` datetime(6) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `dob` date NOT NULL,
  `gender` enum('FEMALE','MALE','OTHERS') NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `joining_date` date DEFAULT NULL,
  `language` enum('BANGLA','ENGLISH','OTHERS') DEFAULT NULL,
  `nid_number` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKepoqvgnjmxe2vwhaxqexfb5y9` (`nid_number`),
  UNIQUE KEY `UKs3a0n2xpua3e28br3nfv2ef2u` (`user_id`),
  KEY `FKm3lxmbag0sten4wtde2k3awf9` (`police_station_id`),
  CONSTRAINT `FKm3lxmbag0sten4wtde2k3awf9` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`),
  CONSTRAINT `FKs9hyblmxy93489wvr18acvhhs` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_officers`
--

LOCK TABLES `sales_officers` WRITE;
/*!40000 ALTER TABLE `sales_officers` DISABLE KEYS */;
INSERT INTO `sales_officers` VALUES (1,'Tanvir_Sales_Core_c4486875-be03-48ec-a787-ecdcecb2dca5.jpg','2026-06-22 03:34:34.827874','Area Distribution Specialist','1999-10-10','MALE','Tanvir_Sales_Core_c4486875-be03-48ec-a787-ecdcecb2dca5.jpg',_binary '','2026-05-01','BANGLA','1999261155412','2026-06-22 03:34:34.827874',21,36,NULL);
/*!40000 ALTER TABLE `sales_officers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27  3:17:51
