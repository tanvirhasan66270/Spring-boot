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
-- Table structure for table `purchase_requisition_tokens`
--

DROP TABLE IF EXISTS `purchase_requisition_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_requisition_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `approval_status` enum('APPROVED','CANCELLED','PENDING','REJECTED') DEFAULT NULL,
  `approved_by` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `product_names` text,
  `purchase_created_at` datetime(6) DEFAULT NULL,
  `purchase_requisition_id` bigint DEFAULT NULL,
  `purchase_updated_at` datetime(6) DEFAULT NULL,
  `quantity_required` int DEFAULT NULL,
  `remarks` text,
  `requested_by` bigint DEFAULT NULL,
  `required_by_date` date DEFAULT NULL,
  `supplier_names` text,
  `token` varchar(200) NOT NULL,
  `total_products` int DEFAULT NULL,
  `urgency_level` enum('CRITICAL','HIGH','LOW','MEDIUM') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK45befj545kup7rqov1gwrgnli` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_requisition_tokens`
--

LOCK TABLES `purchase_requisition_tokens` WRITE;
/*!40000 ALTER TABLE `purchase_requisition_tokens` DISABLE KEYS */;
INSERT INTO `purchase_requisition_tokens` VALUES (1,_binary '','PENDING',NULL,'2026-07-19 14:47:23.824720','USD',NULL,'Heavy Duty Double Needle Sewing Machine, Industrial Garments Big Knitting Machine','2026-07-19 14:47:23.785823',4,'2026-07-19 14:47:23.785823',210,'ssssssssssssssss sesesesesesesesesesesesesese',80,'2026-07-20','Apex Logistics Group, Badrul','9dd63720-3ae5-4d34-b4b1-e4f51f028374',2,'LOW');
/*!40000 ALTER TABLE `purchase_requisition_tokens` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-19 19:07:20
