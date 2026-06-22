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
-- Table structure for table `purchase_requisitions`
--

DROP TABLE IF EXISTS `purchase_requisitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_requisitions` (
  `quantity_required` int NOT NULL,
  `required_by_date` date NOT NULL,
  `approved_by` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `last_updated_at` datetime(6) DEFAULT NULL,
  `requested_by` bigint NOT NULL,
  `currency` varchar(10) NOT NULL,
  `remarks` text,
  `approval_status` enum('APPROVED','CANCELLED','PENDING','REJECTED') NOT NULL,
  `urgency_level` enum('CRITICAL','HIGH','LOW','MEDIUM') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_requisitions`
--

LOCK TABLES `purchase_requisitions` WRITE;
/*!40000 ALTER TABLE `purchase_requisitions` DISABLE KEYS */;
INSERT INTO `purchase_requisitions` VALUES (500,'2026-07-05',NULL,'2026-06-20 03:12:07.000000',1,'2026-06-20 03:12:07.000000',10,'USD','Urgent raw material parts required for line 12 sewing setup.','PENDING','HIGH'),(15,'2026-06-25',NULL,'2026-06-20 03:12:07.000000',2,'2026-06-20 03:12:07.000000',11,'USD','Critical backup server and laptop motherboard chip replacement accessories.','PENDING','CRITICAL'),(1200,'2026-07-20',NULL,'2026-06-20 03:12:07.000000',3,'2026-06-20 03:12:07.000000',12,'USD','Fulfillment distribution items stock replenishment request.','PENDING','MEDIUM'),(25000,'2026-07-10',NULL,'2026-06-20 03:13:20.000000',4,'2026-06-20 03:13:20.000000',14,'USD','Bulk purchase of metal snap buttons and YKK zippers for export denim jacket order.','PENDING','HIGH'),(350,'2026-07-30',NULL,'2026-06-20 03:13:20.000000',5,'2026-06-20 03:13:20.000000',15,'USD','Woven cotton fabric rolls for autumn shirt shipment sampling line.','PENDING','MEDIUM'),(80,'2026-06-28',NULL,'2026-06-20 03:13:20.000000',6,'2026-06-20 03:13:20.000000',10,'USD','Eco-friendly washing plant fixing agent chemicals for bulk fabric wash processing.','PENDING','CRITICAL'),(5000,'2026-08-15',NULL,'2026-06-20 03:13:20.000000',7,'2026-06-20 03:13:20.000000',16,'USD','Corrugated 5-ply export master cartons for final batch garments packing.','PENDING','LOW'),(12,'2026-07-05',NULL,'2026-06-20 03:13:20.000000',8,'2026-06-20 03:13:20.000000',11,'USD','Zebra industrial barcode printer replacement parts for finish goods warehouse.','PENDING','HIGH');
/*!40000 ALTER TABLE `purchase_requisitions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22  6:28:02
