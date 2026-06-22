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
-- Table structure for table `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouses` (
  `capacity` double NOT NULL,
  `is_active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manager_id` bigint DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `location` text NOT NULL,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2qm0l82n5ivhyqwmgejxxefm1` (`name`),
  KEY `FKookqoqrmqnl7j2pibx0pmj2yo` (`police_station_id`),
  CONSTRAINT `FKookqoqrmqnl7j2pibx0pmj2yo` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouses`
--

LOCK TABLES `warehouses` WRITE;
/*!40000 ALTER TABLE `warehouses` DISABLE KEYS */;
INSERT INTO `warehouses` VALUES (75000.5,_binary '','2026-06-19 22:46:35.000000',1,101,NULL,'2026-06-19 22:46:35.000000','Central Apparel Hub','Plot 45, Tongi Industrial Area, Gazipur',''),(45000,_binary '','2026-06-19 22:46:35.000000',2,102,NULL,'2026-06-19 22:46:35.000000','Dhaka Port Fulfillment Center','Road 12, Tejgaon Industrial Area, Dhaka',''),(120000,_binary '','2026-06-19 22:46:35.000000',3,103,NULL,'2026-06-19 22:46:35.000000','Chattogram Export Yard','Zone B, CEPZ, Halishahar, Chattogram',''),(65000,_binary '','2026-06-19 22:47:19.000000',4,104,NULL,'2026-06-19 22:47:19.000000','Narayanganj Raw Material Depot','Bscic Industrial Estate, Panchabati, Narayanganj',''),(30000,_binary '','2026-06-19 22:47:19.000000',5,105,NULL,'2026-06-19 22:47:19.000000','Uttara Sorting & Fulfillment Hub','Sector 18, Uttara, Dhaka',''),(90000.75,_binary '','2026-06-19 22:47:19.000000',6,106,NULL,'2026-06-19 22:47:19.000000','Savar Fabric Storage Yard','Hemayetpur, Savar, Dhaka',''),(40000,_binary '','2026-06-19 22:47:19.000000',7,107,NULL,'2026-06-19 22:47:19.000000','Bogura Regional Distribution Center','Sadar Industrial Area, Bogura',''),(110000,_binary '','2026-06-19 22:47:19.000000',8,108,NULL,'2026-06-19 22:47:19.000000','Benapole Border Transit Warehouse','Port Road, Benapole, Jashore','');
/*!40000 ALTER TABLE `warehouses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-23  3:29:28
