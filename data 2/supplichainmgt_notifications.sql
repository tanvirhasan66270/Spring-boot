-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: supplichainmgt
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `message` text NOT NULL,
  `recipient_id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-07-19 14:47:23.833696',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-4','SHIPMENT'),(2,'2026-07-19 14:47:23.835720',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-4','SHIPMENT'),(3,'2026-07-19 14:47:23.837687',_binary '','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-4','SHIPMENT'),(4,'2026-08-16 00:43:14.396793',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-5','SHIPMENT'),(5,'2026-08-16 00:43:14.398791',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-5','SHIPMENT'),(6,'2026-08-16 00:43:14.399792',_binary '','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-5','SHIPMENT'),(7,'2026-08-16 00:43:46.519891',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-6','SHIPMENT'),(8,'2026-08-16 00:43:46.520983',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-6','SHIPMENT'),(9,'2026-08-16 00:43:46.523108',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-6','SHIPMENT'),(10,'2026-08-16 04:48:26.761832',_binary '\0','Your quotation has been rejected by the procurement team.','83','Quotation REJECTED','QUOTATION'),(11,'2026-08-16 15:21:58.717547',_binary '\0','A new purchase order has been created for you. Please review the details.','36','New Purchase Order #PO-1786872118455','PURCHASE_ORDER'),(12,'2026-08-16 15:21:59.084132',_binary '\0','A new purchase order has been created for you. Please review the details.','36','New Purchase Order #PO-1786872119070','PURCHASE_ORDER');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-16 16:03:00
