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
-- Table structure for table `quotations`
--

DROP TABLE IF EXISTS `quotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quotations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attachment_url` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `delivery_time` date NOT NULL,
  `is_selected` bit(1) NOT NULL,
  `lead_time_days` int NOT NULL,
  `notes` text,
  `product_description` text NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `quotation_number` varchar(50) NOT NULL,
  `received_at` date NOT NULL,
  `status` enum('APPROVED','EXPIRED','PENDING','REJECTED','UNDER_REVIEW') DEFAULT NULL,
  `total_price` double NOT NULL,
  `unit_price` double NOT NULL,
  `valid_until` date NOT NULL,
  `warranty` varchar(255) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `purchase_requisition_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9kbnjdxcf5d7qxwy80ple68bh` (`quotation_number`),
  KEY `FKbfn4c8ah06l9jhg5dakufbr0e` (`product_id`),
  KEY `FK67fnj0t9wpb730h5i30o13viy` (`purchase_requisition_id`),
  KEY `FK9vdoa49dekeoytb3pchlxbmcl` (`supplier_id`),
  CONSTRAINT `FK67fnj0t9wpb730h5i30o13viy` FOREIGN KEY (`purchase_requisition_id`) REFERENCES `purchase_requisitions` (`id`),
  CONSTRAINT `FK9vdoa49dekeoytb3pchlxbmcl` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  CONSTRAINT `FKbfn4c8ah06l9jhg5dakufbr0e` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quotations`
--

LOCK TABLES `quotations` WRITE;
/*!40000 ALTER TABLE `quotations` DISABLE KEYS */;
INSERT INTO `quotations` VALUES (1,'Industrial_Knitting_Machine_Gear_3fdf3d20-30ea-4af5-9843-d859793ac0a8.JPG','2026-07-01 15:58:25.889589','2026-07-15',_binary '\0',14,'Price includes logistics and customs clearing handling costs.','High-grade hardened steel replacement gear components.','Industrial Knitting Machine Gear',15,'QTN-1782899905889','2026-07-01','PENDING',6750,450,'2026-09-30','1 Year Comprehensive',10,1,1),(2,'Industrial_Garments_Big_Knitting_Machine_891c2175-fb29-4083-adb1-df4781c24857.jpg','2026-07-08 14:56:25.970203','2026-07-22',_binary '\0',120,'qamvr ehjrfgbihv','sndbfg qhw3rbfknb1 jewb','Industrial Garments Big Knitting Machine',200,'QTN-1783500985970','2026-07-16','PENDING',100000000,500000,'2026-07-10','3years',2,1,1),(3,'Industrial_Grade_Steel_Pipe_ffc6385d-45ce-4251-981f-0542be49c3c5.JPG','2026-07-08 15:36:05.404090','2026-07-15',_binary '\0',7,'Bulk discount pricing applied based on PR quota requirements.','Seamless structural carbon steel pipes for SCM fluid logistics grid.','Industrial Grade Steel Pipe',50,'QTN-1783503365404','2026-07-08','APPROVED',6025,120.5,'2026-09-30','1 Year Manufacturer Warranty',15,1,1);
/*!40000 ALTER TABLE `quotations` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-08 19:11:50
