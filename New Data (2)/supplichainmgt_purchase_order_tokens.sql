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
-- Table structure for table `purchase_order_tokens`
--

DROP TABLE IF EXISTS `purchase_order_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `expected_delivery_date` date DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `issued_by` bigint DEFAULT NULL,
  `po_number` varchar(255) DEFAULT NULL,
  `purchase_created_at` datetime(6) DEFAULT NULL,
  `purchase_order_id` bigint DEFAULT NULL,
  `purchase_requisition_id` bigint DEFAULT NULL,
  `purchase_updated_at` datetime(6) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `quotation_id` bigint DEFAULT NULL,
  `status` enum('CANCELLED','DRAFT','ISSUED','PARTIALLY_RECEIVED','RECEIVED') DEFAULT NULL,
  `supplier_id` bigint DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `total_amount` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2xc0p4apor4eu33cdc6u3ueep` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_tokens`
--

LOCK TABLES `purchase_order_tokens` WRITE;
/*!40000 ALTER TABLE `purchase_order_tokens` DISABLE KEYS */;
INSERT INTO `purchase_order_tokens` VALUES (1,_binary '','2026-07-15 16:13:20.912193','USD',NULL,'2026-07-16','2026-07-22 16:13:20.912193',80,'PO-1784110400890','2026-07-15 16:13:20.890221',12,1,'2026-07-15 16:13:20.890221',50,3,'DRAFT',1,'33a7610b-5eba-4ec6-8944-56ce63e55322',6025);
/*!40000 ALTER TABLE `purchase_order_tokens` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-15 19:20:45
