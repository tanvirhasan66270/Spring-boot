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
-- Table structure for table `quotations`
--

DROP TABLE IF EXISTS `quotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quotations` (
  `delivery_time` date NOT NULL,
  `is_selected` bit(1) NOT NULL,
  `lead_time_days` int NOT NULL,
  `quantity` int NOT NULL,
  `received_at` date NOT NULL,
  `total_price` double NOT NULL,
  `unit_price` double NOT NULL,
  `valid_until` date NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `purchase_requisition_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  `quotation_number` varchar(50) NOT NULL,
  `warranty` varchar(50) DEFAULT NULL,
  `attachment_url` varchar(512) DEFAULT NULL,
  `notes` text,
  `product_description` text NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `status` enum('APPROVED','EXPIRED','PENDING','REJECTED','UNDER_REVIEW') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9kbnjdxcf5d7qxwy80ple68bh` (`quotation_number`),
  KEY `FKbfn4c8ah06l9jhg5dakufbr0e` (`product_id`),
  KEY `FK67fnj0t9wpb730h5i30o13viy` (`purchase_requisition_id`),
  KEY `FK9vdoa49dekeoytb3pchlxbmcl` (`supplier_id`),
  CONSTRAINT `FK67fnj0t9wpb730h5i30o13viy` FOREIGN KEY (`purchase_requisition_id`) REFERENCES `purchase_requisitions` (`id`),
  CONSTRAINT `FK9vdoa49dekeoytb3pchlxbmcl` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  CONSTRAINT `FKbfn4c8ah06l9jhg5dakufbr0e` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quotations`
--

LOCK TABLES `quotations` WRITE;
/*!40000 ALTER TABLE `quotations` DISABLE KEYS */;
INSERT INTO `quotations` VALUES ('2026-07-05',_binary '\0',10,5000,'2026-06-20',22500,4.5,'2026-08-30','2026-06-20 03:27:28.363889',5,10,5,3,'QTN-1781904448364','No Warranty','100%_Cotton_Single_Jersey_Fabric_06bfc979-5f43-4401-8411-fd4770e74003.jpg','Price is negotiable for bulk orders above 10,000 kg.','High-quality GSM 180 cotton fabric for summer t-shirts.','100% Cotton Single Jersey Fabric','PENDING'),('2026-06-30',_binary '\0',5,12000,'2026-06-19',14400,1.2,'2026-07-15','2026-06-20 03:28:26.105037',7,5,3,2,'QTN-1781904506105','Replacement if damaged during transit','Corrugated_Export_Quality_Carton_Box_14208142-79ea-4ffb-938f-55b5d53496bd.jpg','Samples have been sent to the QA lab for testing.','5-ply heavy-duty master cartons for garment shipment.','Corrugated Export Quality Carton Box','UNDER_REVIEW'),('2026-07-10',_binary '\0',12,1500,'2026-06-18',23250,15.5,'2026-09-01','2026-06-20 03:28:55.838684',8,2,3,2,'QTN-1781904535838','2 Years Shelf Life','Textile_Reactive_Softener_0467e244-edc0-4d08-a833-3d3b7e16b698.jpg','Approved by Chief Operations Officer.','Eco-friendly premium textile softening agent.','Textile Reactive Softener','APPROVED'),('2026-07-25',_binary '\0',20,25000,'2026-06-15',21250,0.85,'2026-07-01','2026-06-20 03:29:20.035472',9,10,2,5,'QTN-1781904560035','1 Year','YKK_Metal_Zipper_20cm_836c5b9a-7f9b-456a-8529-def4bec54dac.jpg','Rejected due to long lead time (20 days). We need it within 7 days.','Original YKK brass zippers for denim pants.','YKK Metal Zipper 20cm','REJECTED');
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

-- Dump completed on 2026-06-23  3:29:26
