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
-- Table structure for table `customer_orders`
--

DROP TABLE IF EXISTS `customer_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cod_amount` double DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `delivery_address` text NOT NULL,
  `delivery_charge` double NOT NULL,
  `estimated_delivery` date DEFAULT NULL,
  `line_total` double NOT NULL,
  `order_number` varchar(50) NOT NULL,
  `quantity` int NOT NULL,
  `service_type` enum('EXPRESS','OVERNIGHT','SAME_DAY','STANDARD') NOT NULL,
  `status` varchar(20) NOT NULL,
  `total_amount` double NOT NULL,
  `unit_price` double NOT NULL,
  `weight` double NOT NULL,
  `customer_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs4wt1sgd48rj6cgahwlksogx` (`order_number`),
  KEY `FK6l43qmx85kv98oigli25si8xh` (`customer_id`),
  KEY `FKgx0lsw9awdfc7vnrvkft7fd9q` (`product_id`),
  CONSTRAINT `FK6l43qmx85kv98oigli25si8xh` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKgx0lsw9awdfc7vnrvkft7fd9q` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_orders`
--

LOCK TABLES `customer_orders` WRITE;
/*!40000 ALTER TABLE `customer_orders` DISABLE KEYS */;
INSERT INTO `customer_orders` VALUES (1,600,'2026-06-21 03:32:00.213672','BDT','House 12, Road 5, Dhanmondi, Dhaka',161.5,'2026-06-25',600,'ORD-1781991120213',5,'EXPRESS','PENDING',761.5,120,2.5,1,3),(2,0,'2026-06-21 04:21:47.909027','USD','Sector 4, Road 7, Uttara, Dhaka',64,'2026-06-26',136.5,'ORD-1781994107909',3,'STANDARD','PENDING',200.5,45.5,1.2,1,2);
/*!40000 ALTER TABLE `customer_orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-21  4:25:14
