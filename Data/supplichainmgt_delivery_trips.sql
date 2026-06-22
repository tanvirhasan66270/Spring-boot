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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_trips`
--

LOCK TABLES `delivery_trips` WRITE;
/*!40000 ALTER TABLE `delivery_trips` DISABLE KEYS */;
INSERT INTO `delivery_trips` VALUES (1,NULL,'2026-06-21 02:01:17.979397','House 78, Road 11, Banani, Dhaka',NULL,'Sultan\'s Dine - Banani Branch',NULL,'Morning Shift (10:30 AM)','Md. Tanvir (Sales Officer)',NULL,'PENDING','Branding materials and corporate gift boxes','2026-06-21 02:01:17.979397','Covered Van - Dhaka Metro SHA-12-9988',2,1,4),(2,NULL,'2026-06-21 02:02:59.945582','Hemayetpur, Savar, Dhaka',NULL,'Sultan\'s Dine Factory Outpost',NULL,'Early Morning Run (06:00 AM)','Nusrat Jahan','2026-06-21 02:02:59.945582','IN_TRANSIT','Bulk storage carton boxes and master packaging rolls','2026-06-21 02:02:59.945582','Heavy Truck - Dhaka Metro TA-14-7766',2,1,4),(3,'2026-06-21 02:03:47.946672','2026-06-21 02:03:47.946672','Block C, Bashundhara R/A, Dhaka','deliveries/DELIVERIES_137cc529-64b3-4f09-b696-23c97f597ab0_CV.jpg','Corporate HQ Warehouse','signatures/SIGNATURES_79f51e23-da75-4476-a5bd-06e7857df121_CV.jpg','Weekend Special Shift','Kamrul Hasan','2026-06-21 02:03:47.946672','DELIVERED','Delivered and verified by floor manager','2026-06-21 02:12:00.824951','Covered Van - Dhaka Metro SHA-22-3344',3,1,5),(4,NULL,'2026-06-21 02:28:37.522598','House 78, Road 11, Banani, Dhaka',NULL,'Sultan\'s Dine - Banani Cloud Kitchen',NULL,'Rescheduled to Afternoon Shift (03:30 PM)','Md. Tanvir (Sales Officer)',NULL,'PENDING','Driver switched to Captain #5 due to sudden sickness of previous driver','2026-06-21 02:28:37.522598','Covered Van - Dhaka Metro SHA-11-0099',1,1,2);
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

-- Dump completed on 2026-06-22  6:28:00
