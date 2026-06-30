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
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_by_email` varchar(255) NOT NULL,
  `captain_registration_number` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estimated_delivery` date NOT NULL,
  `origin` varchar(255) NOT NULL,
  `pod_file_url` varchar(255) DEFAULT NULL,
  `send_by_address` varchar(255) NOT NULL,
  `shipment_number` varchar(50) NOT NULL,
  `transport_cost` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `vehicle_number` varchar(255) NOT NULL,
  `po_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKil6gfafk66ly6rpmjugdmd1ne` (`shipment_number`),
  KEY `FKa4f6mkumdlqftqtlcr3e8p8jw` (`po_id`),
  KEY `FKtgwmjvgiiiws922molg0jnn8o` (`supplier_id`),
  CONSTRAINT `FKa4f6mkumdlqftqtlcr3e8p8jw` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`id`),
  CONSTRAINT `FKtgwmjvgiiiws922molg0jnn8o` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
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
