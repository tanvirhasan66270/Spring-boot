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
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `is_active` bit(1) NOT NULL,
  `quantity` int NOT NULL,
  `reorder_point` int DEFAULT NULL,
  `selling_price` double DEFAULT NULL,
  `unit_cost` double DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `availability` varchar(255) DEFAULT NULL,
  `has_expiry_date` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `product_code` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `image` longtext,
  `weight` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK922x4t23nx64422orei4meb2y` (`product_code`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (_binary '',50,10,23000,18500,37,1,'IN_STOCK','NO','CMF by Nothing Phone 1 (Orange)','PRD-MOB-001','Pcs','data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...',0),(_binary '',120,15,3200,2200,37,2,'IN_STOCK','NO','CMF 65W GaN Fast Charger','PRD-ACC-002','Pcs','data:image/png;base64,aF8fC7R3m9...',0),(_binary '',85,20,2600,1800,37,3,'IN_STOCK','NO','Anker Nano 30W Smart Charger with Display','PRD-ACC-003','Pcs','data:image/png;base64,e8f8fc7...',0),(_binary '',200,30,650,350,37,4,'IN_STOCK','NO','Universal Bike Mobile Mount Holder','PRD-ACC-004','Pcs','data:image/png;base64,9f8fc7...',0),(_binary '',45,12,1850,1100,37,5,'IN_STOCK','NO','Waterproof Motorcycle Phone Mount with QC 3.0 Wireless Charger','PRD-ACC-005','Pcs','data:image/png;base64,2fb778a...',0),(_binary '',8,2,58000,45000,26,6,'IN_STOCK','NO','Industrial Double Needle Overlock Sewing Machine','PRD-MAC-006','Set','data:image/png;base64,7b1420...',0),(_binary '',3,1,350000,280000,27,7,'IN_STOCK','NO','Heavy Duty Automatic Fabric Inspection Machine','PRD-MAC-007','Set','data:image/png;base64,fugg4g48...',0),(_binary '',12,3,68000,52000,26,8,'IN_STOCK','NO','High Speed Flatbed Interlock Garment Sewing Machine','PRD-MAC-008','Set','data:image/png;base64,8m3m2a...',0),(_binary '',2,1,820000,650000,25,9,'IN_STOCK','NO','Industrial Textile Washing and Dyeing Machine (Large)','PRD-MAC-009','Set','data:image/png;base64,ay5cas...',0),(_binary '',100,5,6000,3500,34,10,'AVAILABLE','NO','Enterprise Laptop Motherboard Chip-Level Repair Service','PRD-SRV-010','Job','data:image/png;base64,14202fb...',0);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22  6:28:00
