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
-- Table structure for table `payment_statements`
--

DROP TABLE IF EXISTS `payment_statements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_statements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `customer_account_number` varchar(255) DEFAULT NULL,
  `issue_status` enum('CONFIRMED_BY_OFFICER','FAILED_OR_REJECTED','PENDING_VERIFICATION') NOT NULL,
  `paid_amount` double NOT NULL,
  `payment_check_image` varchar(255) DEFAULT NULL,
  `payment_method` enum('BANK','BKASH','CASH','NAGAD','ROCKET') DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlv3r5cjxbqmivu9nmr6qdev27` (`customer_order_id`),
  CONSTRAINT `FKlv3r5cjxbqmivu9nmr6qdev27` FOREIGN KEY (`customer_order_id`) REFERENCES `customer_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_statements`
--

LOCK TABLES `payment_statements` WRITE;
/*!40000 ALTER TABLE `payment_statements` DISABLE KEYS */;
INSERT INTO `payment_statements` VALUES (1,'2026-08-13 15:33:11.918772','6541321456415641','CONFIRMED_BY_OFFICER',573415,'ORD-1785668605191_e49f5384-4733-4251-86ff-ac51d1836807.jpg','BANK','TXN-1786613591918','2026-08-13 15:34:59.590005',5);
/*!40000 ALTER TABLE `payment_statements` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-16 16:02:55
