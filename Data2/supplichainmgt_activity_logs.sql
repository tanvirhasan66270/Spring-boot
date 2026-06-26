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
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `description` text,
  `ip_address` varchar(255) DEFAULT NULL,
  `module` varchar(255) NOT NULL,
  `performed_at` datetime(6) NOT NULL,
  `reference_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `action_status` enum('FAILED','SUCCESS','WARNING') NOT NULL,
  `new_value` text,
  `old_value` text,
  `user_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
INSERT INTO `activity_logs` VALUES (1,'CREATE','Logistics Officer generated daily operational report for Node: WH-CTG-PORT-02','0:0:0:0:0:0:0:1','DAILY_REPORT','2026-06-23 03:19:49.352871','2','16','FAILED',NULL,NULL,NULL),(2,'CREATE','New Letter of Credit successfully initiated by Commercial Officer. LC Number: TRN-1782497071527','0:0:0:0:0:0:0:1','LC','2026-06-27 00:04:32.955586','2','16','SUCCESS','{\"status\":\"DRAFT\"}',NULL,NULL),(3,'UPDATE','General metadata or Status updated for LC Number: TRN-1782497071527 -> Status: DRAFT','0:0:0:0:0:0:0:1','LC','2026-06-27 00:07:41.390402','2','16','SUCCESS','{\"issuingBank\":\"Global Trade Bank\", \"status\":\"DRAFT\"}','{\"issuingBank\":\"Global Trade Bank\", \"status\":\"DRAFT\"}',NULL),(4,'UPDATE','Official commercial amendment applied. LC Number: TRN-1782497071527, Total Amendment Count: 0','0:0:0:0:0:0:0:1','LC','2026-06-27 00:49:50.388842','2','16','SUCCESS','{\"amount\":4500000.0}','{\"amount\":450000.0}',NULL),(5,'UPDATE','General metadata or Status updated for LC Number: TRN-1782497071527 -> Status: DRAFT','0:0:0:0:0:0:0:1','LC','2026-06-27 00:50:38.304577','2','16','SUCCESS','{\"issuingBank\":\"Global Trade Bank\", \"status\":\"DRAFT\"}','{\"issuingBank\":\"Global Trade Bank\", \"status\":\"DRAFT\"}',NULL),(6,'UPDATE','Official commercial amendment applied. LC Number: TRN-1782497071527, Total Amendment Count: 1','0:0:0:0:0:0:0:1','LC','2026-06-27 01:13:43.918236','2','16','SUCCESS','{\"amount\":4500000.0}','{\"amount\":4500000.0}',NULL),(7,'CREATE','New Product successfully created. Code: PROD-2026-091, Name: Industrial Safety Valve','0:0:0:0:0:0:0:1','PRODUCT','2026-06-27 01:16:37.819704','11','16','SUCCESS','{\"productCode\":\"PROD-2026-091\", \"name\":\"Industrial Safety Valve\"}',NULL,NULL),(8,'CREATE','New multi-item customer order placed. Order Number: ORD-1782502202697, Total Amount: 378.0 BDT','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','2026-06-27 01:30:08.200369','3','16','SUCCESS','{\"orderNumber\":\"ORD-1782502202697\", \"totalAmount\":378.0}',NULL,NULL),(9,'CREATE','New multi-item customer order placed. Order Number: ORD-1782503622633, Total Amount: 378.0 BDT','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','2026-06-27 01:53:48.357479','4','1','SUCCESS','{\"orderNumber\":\"ORD-1782503622633\", \"totalAmount\":378.0}',NULL,NULL),(10,'CREATE','New Invoice successfully generated. Invoice Number: TRN-1782504600025, Total Amount: 1193.0','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:10:00.159811','7','Unnon','SUCCESS','{\"invoiceNumber\":\"TRN-1782504600025\", \"status\":\"ISSUED\", \"grandTotal\":1193.0}',NULL,NULL),(11,'DELETE','Invoice node permanently purged from billing system ledger. Invoice Number was: TRN-1782504281037','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:10:51.097027','6','Unnon','SUCCESS',NULL,'{\"invoiceNumber\":\"TRN-1782504281037\", \"grandTotal\":1193.0}',NULL),(12,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:13:18.068887','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"ISSUED\", \"grandTotal\":1193.0}',NULL),(13,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:17:19.740522','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(14,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:18:48.680513','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(15,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:18:58.338846','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(16,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:30:57.037458','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(17,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:42:30.664280','7','Unnon','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(18,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:48:37.036516','7','1','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(19,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:48:57.576182','7','1','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL),(20,'UPDATE','Invoice metadata or settlement status updated. Invoice Number: TRN-1782504600025','0:0:0:0:0:0:0:1','INVOICE','2026-06-27 02:54:56.915250','7','1','SUCCESS','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}','{\"status\":\"CANCELLED\", \"grandTotal\":1193.0}',NULL);
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27  3:17:44
