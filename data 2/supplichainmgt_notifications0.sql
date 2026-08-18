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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-07-19 14:47:23.833696',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-4','SHIPMENT'),(2,'2026-07-19 14:47:23.835720',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-4','SHIPMENT'),(3,'2026-07-19 14:47:23.837687',_binary '','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-4','SHIPMENT'),(4,'2026-08-16 00:43:14.396793',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-5','SHIPMENT'),(5,'2026-08-16 00:43:14.398791',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-5','SHIPMENT'),(6,'2026-08-16 00:43:14.399792',_binary '','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-5','SHIPMENT'),(7,'2026-08-16 00:43:46.519891',_binary '','A new purchase requisition has been submitted and is pending approval.','82','New Purchase Requisition #PRQ-6','SHIPMENT'),(8,'2026-08-16 00:43:46.520983',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','36','New RFQ Invitation #PRQ-6','SHIPMENT'),(9,'2026-08-16 00:43:46.523108',_binary '\0','You have been selected as a target vendor for a new procurement requirement. Please check your bidding board.','83','New RFQ Invitation #PRQ-6','SHIPMENT'),(10,'2026-08-16 04:48:26.761832',_binary '\0','Your quotation has been rejected by the procurement team.','83','Quotation REJECTED','QUOTATION'),(11,'2026-08-16 15:21:58.717547',_binary '\0','A new purchase order has been created for you. Please review the details.','36','New Purchase Order #PO-1786872118455','PURCHASE_ORDER'),(12,'2026-08-16 15:21:59.084132',_binary '\0','A new purchase order has been created for you. Please review the details.','36','New Purchase Order #PO-1786872119070','PURCHASE_ORDER'),(13,'2026-08-16 17:30:00.207659',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','PROCUREMENT','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(14,'2026-08-16 17:30:00.210630',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','LOGISTICS_OFFICER','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(15,'2026-08-16 17:30:00.212632',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','MANAGER','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(16,'2026-08-16 17:30:00.218629',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','19','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(17,'2026-08-16 17:30:00.219628',_binary '','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','66','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(18,'2026-08-16 17:30:00.221659',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','80','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(19,'2026-08-16 17:30:00.222632',_binary '\0','New shipment (SH-302BEAF2) dispatched by supplier Apex Logistics Group. PO Ref: #15, Vehicle: DHAKA-METRO-TA-11-2233, Destination: asdfdafa.','82','New Cargo Shipment Dispatched: SH-302BEAF2','SHIPMENT'),(20,'2026-08-17 00:10:05.948680',_binary '\0','A new payment of 9000.0 has been added for Customer Order: ORD-1785669559228','COMMERCIAL_OFFICER','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(21,'2026-08-17 00:10:05.965184',_binary '\0','A new payment of 9000.0 has been added for Customer Order: ORD-1785669559228','16','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(22,'2026-08-17 00:10:05.971169',_binary '\0','A new payment of 9000.0 has been added for Customer Order: ORD-1785669559228','59','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(23,'2026-08-17 00:10:05.973182',_binary '\0','A new payment of 9000.0 has been added for Customer Order: ORD-1785669559228','62','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(24,'2026-08-17 00:10:05.973182',_binary '\0','A new payment of 9000.0 has been added for Customer Order: ORD-1785669559228','88','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(25,'2026-08-17 00:48:40.683236',_binary '\0','New QC Inspection (FUNCTIONAL) performed for GRN #GRN-C739DBDC, Product: Heavy Duty Double Needle Sewing Machine. Result: VERY_GOOD (Defects: 2/10).','LOGISTICS_OFFICER','New QC Inspection Created: #1','QC_INSPECTION'),(26,'2026-08-17 00:48:40.683236',_binary '\0','New QC Inspection (FUNCTIONAL) performed for GRN #GRN-C739DBDC, Product: Heavy Duty Double Needle Sewing Machine. Result: VERY_GOOD (Defects: 2/10).','MANAGER','New QC Inspection Created: #1','QC_INSPECTION'),(27,'2026-08-17 00:48:40.691787',_binary '\0','New QC Inspection (FUNCTIONAL) performed for GRN #GRN-C739DBDC, Product: Heavy Duty Double Needle Sewing Machine. Result: VERY_GOOD (Defects: 2/10).','19','New QC Inspection Created: #1','QC_INSPECTION'),(28,'2026-08-17 00:48:40.691787',_binary '\0','New QC Inspection (FUNCTIONAL) performed for GRN #GRN-C739DBDC, Product: Heavy Duty Double Needle Sewing Machine. Result: VERY_GOOD (Defects: 2/10).','66','New QC Inspection Created: #1','QC_INSPECTION'),(29,'2026-08-17 00:48:40.691787',_binary '\0','New QC Inspection (FUNCTIONAL) performed for GRN #GRN-C739DBDC, Product: Heavy Duty Double Needle Sewing Machine. Result: VERY_GOOD (Defects: 2/10).','82','New QC Inspection Created: #1','QC_INSPECTION'),(30,'2026-08-17 10:23:11.714880',_binary '\0','Your quotation has been under_review by the procurement team.','36','Quotation UNDER_REVIEW','QUOTATION'),(31,'2026-08-17 10:35:21.797325',_binary '\0','A new payment of 9805.0 has been added for Customer Order: ORD-1785669559228','COMMERCIAL_OFFICER','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(32,'2026-08-17 10:35:21.826564',_binary '\0','A new payment of 9805.0 has been added for Customer Order: ORD-1785669559228','16','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(33,'2026-08-17 10:35:21.834797',_binary '\0','A new payment of 9805.0 has been added for Customer Order: ORD-1785669559228','59','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(34,'2026-08-17 10:35:21.836280',_binary '\0','A new payment of 9805.0 has been added for Customer Order: ORD-1785669559228','62','New Payment Added: Order #ORD-1785669559228','PAYMENT'),(35,'2026-08-17 10:35:21.836280',_binary '\0','A new payment of 9805.0 has been added for Customer Order: ORD-1785669559228','88','New Payment Added: Order #ORD-1785669559228','PAYMENT');
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

-- Dump completed on 2026-08-18 16:01:13
