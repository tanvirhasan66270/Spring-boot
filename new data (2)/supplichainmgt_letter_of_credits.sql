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
-- Table structure for table `letter_of_credits`
--

DROP TABLE IF EXISTS `letter_of_credits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `letter_of_credits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amendment_count` int NOT NULL,
  `amount` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `currency` varchar(255) NOT NULL,
  `document_vault_url` varchar(255) DEFAULT NULL,
  `expiry_date` date NOT NULL,
  `issuing_bank` varchar(255) NOT NULL,
  `latest_shipment_date` date NOT NULL,
  `lc_number` varchar(255) NOT NULL,
  `lc_status` enum('AMENDED','DRAFT','EXPIRED','OPENED') DEFAULT NULL,
  `opened_at` date DEFAULT NULL,
  `po_number` varchar(255) DEFAULT NULL,
  `port_of_discharge` varchar(255) NOT NULL,
  `port_of_loading` varchar(255) NOT NULL,
  `shipment_inco_terms` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `purchase_order_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  `issuing_bank_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5j0rpl8thfnjg41ae4suwgw3n` (`lc_number`),
  KEY `FKhoo8j70916dkfjeyskagde5vd` (`purchase_order_id`),
  KEY `FKq20c9derecne2dqbjpxwdiq74` (`supplier_id`),
  KEY `FKqymcodlnf7g5fakmnbint3pwk` (`issuing_bank_id`),
  CONSTRAINT `FKhoo8j70916dkfjeyskagde5vd` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`),
  CONSTRAINT `FKq20c9derecne2dqbjpxwdiq74` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  CONSTRAINT `FKqymcodlnf7g5fakmnbint3pwk` FOREIGN KEY (`issuing_bank_id`) REFERENCES `lc-banks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `letter_of_credits`
--

LOCK TABLES `letter_of_credits` WRITE;
/*!40000 ALTER TABLE `letter_of_credits` DISABLE KEYS */;
/*!40000 ALTER TABLE `letter_of_credits` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-09 19:17:47
