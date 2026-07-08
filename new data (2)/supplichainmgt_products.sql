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
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `availability` varchar(255) DEFAULT NULL,
  `has_expiry_date` varchar(255) DEFAULT NULL,
  `image` longtext,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `product_code` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `reorder_point` int NOT NULL,
  `selling_price` double NOT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `unit_cost` double NOT NULL,
  `weight` double NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK922x4t23nx64422orei4meb2y` (`product_code`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'AVAILABLE','NO','uploads/products/double_selai_machin.jpg',_binary '','Heavy Duty Double Needle Sewing Machine','PRD-GAR-001',15,5,32000,'PCS',25000,45.5,1),(2,'AVAILABLE','NO','uploads/products/garments_big_machin.jpg',_binary '','Industrial Garments Big Knitting Machine','PRD-GAR-002',6,2,110000,'PCS',85000,120,1),(3,'AVAILABLE','NO','uploads/products/garments_machin_selai.jpg',_binary '','High Speed Garments Stitching System','PRD-GAR-003',22,8,24000,'PCS',18000,38.2,1),(4,'AVAILABLE','NO','uploads/products/garments_washing_machin.jpg',_binary '','Commercial Garments Washing Machine','PRD-GAR-004',8,3,72000,'PCS',55000,95,1),(5,'AVAILABLE','NO','uploads/products/image_1.jpg',_binary '','Automatic Thread Trimming Overlock Device','PRD-GAR-005',30,10,16500,'PCS',12000,25,1),(6,'AVAILABLE','NO','uploads/products/Mobile_handel.jpg',_binary '','Ergonomic Desktop Mobile Handle Stand','PRD-CEL-006',200,50,450,'PCS',250,0.15,2),(7,'AVAILABLE','NO','uploads/products/protect_cavor.jpg',_binary '','Premium Heavy Duty Silicone Protection Cover','PRD-CEL-007',500,100,290,'PCS',120,0.05,2),(8,'AVAILABLE','NO','uploads/products/Chirger_2.jpg',_binary '','Super Fast Dual Port Charger 2.0','PRD-CEL-008',150,40,850,'PCS',450,0.08,2),(9,'AVAILABLE','NO','uploads/products/a-beautiful-picture-of-the-eiffel-tower.jpg',_binary '','Eiffel Tower Souvenir Tech Desk Ornament','PRD-CEL-009',45,15,1200,'PCS',600,0.35,2),(10,'AVAILABLE','NO','uploads/products/cable_type_c.jpg',_binary '','Type-C Braided Nylon Fast Charging Cable','PRD-CEL-010',350,80,220,'PCS',80,0.03,2),(11,'AVAILABLE','NO','default.jpg',_binary '','Reinforced Steel Thread Spool Holder','PRD-IND-011',85,20,650,'PCS',350,1.2,3),(12,'AVAILABLE','NO','default.jpg',_binary '','Heavy Clutch Motor for Sewing Machine','PRD-IND-012',18,6,6200,'PCS',4500,14.5,3),(13,'AVAILABLE','NO','default.jpg',_binary '','Rotary Hook Assembly Gear','PRD-IND-013',120,25,1400,'PCS',850,0.22,3),(14,'AVAILABLE','NO','default.jpg',_binary '','Industrial Conveyor Drive Belt','PRD-IND-014',60,15,1800,'MTR',1100,2.1,3),(15,'AVAILABLE','NO','default.jpg',_binary '','Pneumatic Air Pressure Regulator Node','PRD-IND-015',14,5,4800,'PCS',3200,0.95,3),(16,'AVAILABLE','NO','default.jpg',_binary '','Heavy Duty Corrugated Carton Cluster','PRD-PKG-016',2500,500,75,'BOX',45,0.4,4),(17,'AVAILABLE','NO','default.jpg',_binary '','Industrial Stretch Shrink Wrap Roll','PRD-PKG-017',180,40,950,'ROLL',650,4.5,4),(18,'AVAILABLE','NO','default.jpg',_binary '','Anti-Static Bubble Wrap Roll Matrix','PRD-PKG-018',55,15,1750,'ROLL',1200,3.8,4),(19,'AVAILABLE','NO','default.jpg',_binary '','Heavy Cargo Nylon Strapping Band','PRD-PKG-019',140,30,680,'ROLL',400,5,4),(20,'AVAILABLE','NO','default.jpg',_binary '','Polypropylene Euro Container Shipping Box','PRD-PKG-020',240,60,520,'PCS',300,1.8,4),(21,'AVAILABLE','YES','default.jpg',_binary '','High-Purity Silicon Lubricant Spray','PRD-CHM-021',210,50,350,'CAN',180,0.3,5),(22,'AVAILABLE','YES','default.jpg',_binary '','Industrial Machine Degreaser Fluid','PRD-CHM-022',90,20,750,'LIT',450,1,5),(23,'AVAILABLE','YES','default.jpg',_binary '','Premium Anti-Rust Coating Liquid','PRD-CHM-023',40,15,1300,'LIT',850,1.05,5),(24,'AVAILABLE','YES','default.jpg',_binary '','Textile Fabric Stain Remover Node','PRD-CHM-024',115,35,420,'CAN',220,0.4,5),(25,'AVAILABLE','YES','default.jpg',_binary '','Universal Machine Bearing Grease Tub','PRD-CHM-025',70,25,590,'KG',380,1,5),(26,'AVAILABLE','NO','default.jpg',_binary '','Digital Vernier Caliper Gauge','PRD-ENG-026',35,12,2900,'PCS',1850,0.25,6),(27,'AVAILABLE','NO','default.jpg',_binary '','Laser Infrared Surface Thermometer','PRD-ENG-027',20,8,3800,'PCS',2400,0.18,6),(28,'AVAILABLE','NO','default.jpg',_binary '','Electronic Digital Weighing Scale Node','PRD-ENG-028',11,4,9200,'PCS',6500,8.5,6),(29,'AVAILABLE','NO','default.jpg',_binary '','Fabric GSM Circular Cutter Plate','PRD-ENG-029',15,6,4500,'PCS',3100,1.4,6),(30,'AVAILABLE','NO','default.jpg',_binary '','Handheld Digital Tacho-Rotation Meter','PRD-ENG-030',12,5,4200,'PCS',2800,0.3,6),(61,'AVAILABLE','NO','Industrial_Sewing_Machine_Motor_ab844d7c-37b3-442c-8b6b-2a941c316c65.JPG',_binary '\0','Industrial Sewing Machine Motor','PRD-ELC-092',50,10,6200,'PCS',4500,12.5,1),(62,'AVAILABLE','NO','user_for_making_dress_3cb92d98-73ec-422a-9f57-9929308d447d.jpg',_binary '\0','febricks','COD-541',100,50000,2000,'PCS',200,2.5,2);
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

-- Dump completed on 2026-07-08 19:11:51
