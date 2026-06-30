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
-- Table structure for table `purchase_orders`
--

DROP TABLE IF EXISTS `purchase_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `currency` varchar(255) NOT NULL,
  `expected_delivery_date` date NOT NULL,
  `issued_by` bigint NOT NULL,
  `po_number` varchar(255) NOT NULL,
  `quantity` int DEFAULT NULL,
  `status` enum('CANCELLED','DRAFT','ISSUED','PARTIALLY_RECEIVED','RECEIVED') NOT NULL,
  `total_amount` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `purchase_requisition_id` bigint NOT NULL,
  `quotation_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpbiykvcpyg0jslne4gviyeuc2` (`po_number`),
  KEY `FKot5qbu5etmpf023gnas0s6vlx` (`purchase_requisition_id`),
  KEY `FKfpfqpr3fugg4g48m3m2ar6ay5` (`quotation_id`),
  KEY `FKrpdasmb8y8xs5tiy4369xpinq` (`supplier_id`),
  CONSTRAINT `FKfpfqpr3fugg4g48m3m2ar6ay5` FOREIGN KEY (`quotation_id`) REFERENCES `quotations` (`id`),
  CONSTRAINT `FKot5qbu5etmpf023gnas0s6vlx` FOREIGN KEY (`purchase_requisition_id`) REFERENCES `purchase_requisitions` (`id`),
  CONSTRAINT `FKrpdasmb8y8xs5tiy4369xpinq` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_orders`
--

LOCK TABLES `purchase_orders` WRITE;
/*!40000 ALTER TABLE `purchase_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 19:16:50
