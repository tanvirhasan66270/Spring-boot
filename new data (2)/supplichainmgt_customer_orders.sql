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
-- Table structure for table `customer_orders`
--

DROP TABLE IF EXISTS `customer_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cod_amount` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `currency` varchar(255) NOT NULL,
  `customer_email` varchar(255) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `delivery_address` text NOT NULL,
  `delivery_charge` double NOT NULL,
  `estimated_delivery` date DEFAULT NULL,
  `item_subtotal` double NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `paid_amount` varchar(255) NOT NULL,
  `service_type` enum('EXPRESS','OVERNIGHT','SAME_DAY','STANDARD') DEFAULT NULL,
  `status` enum('CANCELLED','CONFIRMED','DELIVERED','PENDING','SHIPPED') DEFAULT NULL,
  `total_amount` double NOT NULL,
  `weight` double NOT NULL,
  `customer_id` bigint NOT NULL,
  `delivery_phone` varchar(255) NOT NULL,
  `due_amount` varchar(255) DEFAULT NULL,
  `payment_method` enum('BANK','BKASH','CASH','NAGAD','ROCKET') DEFAULT NULL,
  `payment_status` enum('PAID','PARTIALLY_PAID','REFUNDED','UNPAID') DEFAULT NULL,
  `remarks` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs4wt1sgd48rj6cgahwlksogx` (`order_number`),
  KEY `FK6l43qmx85kv98oigli25si8xh` (`customer_id`),
  CONSTRAINT `FK6l43qmx85kv98oigli25si8xh` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_orders`
--

LOCK TABLES `customer_orders` WRITE;
/*!40000 ALTER TABLE `customer_orders` DISABLE KEYS */;
INSERT INTO `customer_orders` VALUES (1,1500,'2026-06-30 22:58:41.455336','BDT','tanvirhasan66270@gmail.com','Rashed Khan','House 12, Road 5, Banani, Dhaka',2463.7,'2026-07-05',1500,'ORD-1782838721455','2463.70','STANDARD','PENDING',3963.7,120.06,37,'',NULL,NULL,NULL,NULL),(2,1500,'2026-06-30 23:25:01.520597','BDT','tanvirhasan66270@gmail.com','Rashed Khan','House 12, Road 5, Banani, Dhaka',2463.7,'2026-07-05',1500,'ORD-1782840301520','2463.70','STANDARD','PENDING',3963.7,120.06,37,'',NULL,NULL,NULL,NULL),(4,3500,'2026-07-01 15:51:45.073336','BDT','tanvirhasan66270@gmail.com','Rashed Khan','House 14, Road 5, Block D, Dhanmondi, Dhaka',3812.5,'2026-07-06',136000,'ORD-1782899505073','136312.50','STANDARD','PENDING',139812.5,186,37,'',NULL,NULL,NULL,NULL);
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

-- Dump completed on 2026-07-08 19:11:52
