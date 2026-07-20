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
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `action_status` enum('FAILED','SUCCESS','WARNING') NOT NULL,
  `description` text,
  `ip_address` varchar(255) DEFAULT NULL,
  `module` varchar(255) NOT NULL,
  `new_value` text,
  `old_value` text,
  `performed_at` datetime(6) NOT NULL,
  `reference_id` varchar(255) NOT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
INSERT INTO `activity_logs` VALUES (1,'CREATE','SUCCESS','New multi-item customer order placed. Order Number: ORD-1782838721455, Total Amount: 3963.7 BDT','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','{\"orderNumber\":\"ORD-1782838721455\", \"totalAmount\":3963.7}',NULL,'2026-06-30 22:58:48.382478','1',NULL,'16'),(2,'CREATE','SUCCESS','New order placed. Order Number: ORD-1782840301520, Total: 3963.7','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','{\"orderNumber\":\"ORD-1782840301520\", \"totalAmount\":3963.7}',NULL,'2026-06-30 23:25:07.271103','2',NULL,'16'),(3,'CREATE','SUCCESS','New order placed. Order Number: ORD-1782897159704, Total: 139812.5','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','{\"orderNumber\":\"ORD-1782897159704\", \"totalAmount\":139812.5}',NULL,'2026-07-01 15:12:45.290592','3',NULL,'16'),(4,'DELETE','SUCCESS','Customer order permanently purged from logistics cluster. Order Number was: ORD-1782897159704','0:0:0:0:0:0:0:1','CUSTOMER_ORDER',NULL,'{\"orderNumber\":\"ORD-1782897159704\", \"totalAmount\":139812.5}','2026-07-01 15:13:37.482286','3',NULL,'16'),(5,'CREATE','SUCCESS','New order placed. Order Number: ORD-1782899505073, Total: 139812.5','0:0:0:0:0:0:0:1','CUSTOMER_ORDER','{\"orderNumber\":\"ORD-1782899505073\", \"totalAmount\":139812.5}',NULL,'2026-07-01 15:51:49.865965','4',NULL,'16'),(6,'CREATE','SUCCESS','New Product successfully created. Code: PRD-ELC-092, Name: Industrial Sewing Machine Motor','0:0:0:0:0:0:0:1','PRODUCT','{\"productCode\":\"PRD-ELC-092\", \"name\":\"Industrial Sewing Machine Motor\"}',NULL,'2026-07-01 15:58:06.808879','61',NULL,'16'),(7,'CREATE','SUCCESS','New Product successfully created. Code: COD-541, Name: user for making dress','0:0:0:0:0:0:0:1','PRODUCT','{\"productCode\":\"COD-541\", \"name\":\"user for making dress\"}',NULL,'2026-07-06 15:59:29.372744','62',NULL,'16'),(8,'UPDATE','SUCCESS','Product metadata updated for Code: COD-541','0:0:0:0:0:0:0:1','PRODUCT','{\"name\":\"febricks\", \"code\":\"COD-541\", \"categoryId\":2}','{\"name\":\"user for making dress\", \"code\":\"COD-541\", \"categoryId\":2}','2026-07-06 16:03:14.447435','62',NULL,'16'),(9,'UPDATE','SUCCESS','Product metadata updated for Code: COD-541','0:0:0:0:0:0:0:1','PRODUCT','{\"name\":\"febricks\", \"code\":\"COD-541\", \"categoryId\":2}','{\"name\":\"febricks\", \"code\":\"COD-541\", \"categoryId\":2}','2026-07-06 16:04:17.527907','62',NULL,'16'),(10,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:36:21.309671','1',NULL,'UNKNOWN_USER'),(11,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-02','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:38:38.836153','2',NULL,'UNKNOWN_USER'),(12,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:50:13.454017','3',NULL,'tanvirhasan66270@gmail.com'),(13,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:50:22.270381','4',NULL,'tanvirhasan66270@gmail.com'),(14,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:59:31.182303','5',NULL,'tanvirhasan66270@gmail.com'),(15,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 18:59:33.310758','6',NULL,'tanvirhasan66270@gmail.com'),(16,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:04.900271','7',NULL,'tanvirhasan66270@gmail.com'),(17,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:05.181435','10',NULL,'tanvirhasan66270@gmail.com'),(18,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:06.210732','8',NULL,'tanvirhasan66270@gmail.com'),(19,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:07.221289','9',NULL,'tanvirhasan66270@gmail.com'),(20,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:07.481707','12',NULL,'tanvirhasan66270@gmail.com'),(21,'CREATE','SUCCESS','Logistics Officer generated daily operational report for Node: WH-DHAKA-01','0:0:0:0:0:0:0:1','DAILY_REPORT','SUBMITTED',NULL,'2026-07-11 19:00:08.062736','11',NULL,'tanvirhasan66270@gmail.com'),(22,'APPROVE','SUCCESS','Manager approved requisition for 1 suppliers.','0:0:0:0:0:0:0:1','PURCHASE_REQUISITION','APPROVED','PENDING','2026-07-19 14:39:29.420604','1','tanvirhasan66270@gmail.com','82'),(23,'APPROVE','SUCCESS','Manager approved requisition for 2 suppliers.','0:0:0:0:0:0:0:1','PURCHASE_REQUISITION','APPROVED','PENDING','2026-07-19 14:48:41.638895','4','tanvirhasan66270@gmail.com','82'),(24,'APPROVE','SUCCESS','Manager approved requisition for 2 suppliers.','0:0:0:0:0:0:0:1','PURCHASE_REQUISITION','APPROVED','PENDING','2026-07-19 14:48:48.481180','4','tanvirhasan66270@gmail.com','82');
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

-- Dump completed on 2026-07-19 19:07:21
