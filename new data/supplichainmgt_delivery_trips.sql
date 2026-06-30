-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: supplichainmgt
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `delivery_trips`
--

DROP TABLE IF EXISTS `delivery_trips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_trips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_address` varchar(255) NOT NULL,
  `delivery_photo_url` varchar(255) DEFAULT NULL,
  `destination_info` varchar(255) DEFAULT NULL,
  `recipient_signature` varchar(255) DEFAULT NULL,
  `schedule_info` varchar(255) DEFAULT NULL,
  `send_by` varchar(255) NOT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','DELIVERED','IN_TRANSIT','PENDING') NOT NULL,
  `trip_info` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `vehicle_info` varchar(255) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `driver_id` bigint NOT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2o3mj1p41rujvmfdplp1mj0nx` (`customer_id`),
  KEY `FKr449c60k54t53nlem00r869t3` (`driver_id`),
  KEY `FKmjs8olu2fy49vw2m6vbfveoey` (`vehicle_id`),
  CONSTRAINT `FK2o3mj1p41rujvmfdplp1mj0nx` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKmjs8olu2fy49vw2m6vbfveoey` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `FKr449c60k54t53nlem00r869t3` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_trips`
--

LOCK TABLES `delivery_trips` WRITE;
/*!40000 ALTER TABLE `delivery_trips` DISABLE KEYS */;
/*!40000 ALTER TABLE `delivery_trips` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 19:16:49
