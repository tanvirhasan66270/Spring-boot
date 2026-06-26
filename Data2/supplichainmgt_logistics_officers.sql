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
-- Table structure for table `logistics_officers`
--

DROP TABLE IF EXISTS `logistics_officers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logistics_officers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `contact_person` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `dob` date NOT NULL,
  `gender` enum('FEMALE','MALE','OTHERS') NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `joining_date` date DEFAULT NULL,
  `language` enum('BANGLA','ENGLISH','OTHERS') DEFAULT NULL,
  `nid_number` varchar(255) NOT NULL,
  `passport_number` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcy05ijhk7kjrhfvdajftp9jlm` (`nid_number`),
  UNIQUE KEY `UK3evy0tmt052kgxl30e1btaeln` (`user_id`),
  UNIQUE KEY `UKeyc1ews9maiej04o5xnqws9ft` (`passport_number`),
  KEY `FKda83q98bup14mycuf3hl13c6u` (`police_station_id`),
  CONSTRAINT `FKda83q98bup14mycuf3hl13c6u` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`),
  CONSTRAINT `FKmigcgicdln6unhoavd4b46hj` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistics_officers`
--

LOCK TABLES `logistics_officers` WRITE;
/*!40000 ALTER TABLE `logistics_officers` DISABLE KEYS */;
/*!40000 ALTER TABLE `logistics_officers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27  3:17:46
