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
-- Table structure for table `procurement_officers`
--

DROP TABLE IF EXISTS `procurement_officers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `procurement_officers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `created_at` datetime(6) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `dob` date NOT NULL,
  `gender` enum('FEMALE','MALE','OTHERS') NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `joining_date` date DEFAULT NULL,
  `language` enum('BANGLA','ENGLISH','OTHERS') DEFAULT NULL,
  `nid_number` varchar(50) NOT NULL,
  `passport_number` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpifnfi65jle71i1p2tc0ge21` (`nid_number`),
  UNIQUE KEY `UKq3y9cooanj76msh18p8qcjkbu` (`user_id`),
  UNIQUE KEY `UKr1tul9kj1lhuf9dqegbr9dw1i` (`passport_number`),
  KEY `FKtb3ylaq954bjqa9rn53yi39gv` (`police_station_id`),
  CONSTRAINT `FKbtak7w23vatx1m81ai7ntgvp6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKtb3ylaq954bjqa9rn53yi39gv` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `procurement_officers`
--

LOCK TABLES `procurement_officers` WRITE;
/*!40000 ALTER TABLE `procurement_officers` DISABLE KEYS */;
INSERT INTO `procurement_officers` VALUES (1,'Tanvir_Procurement_Node_319b2b44-8f1f-4bf5-bdaf-5f1afdf934c9.jpg','2026-06-22 03:01:41.003594','Strategic Sourcing Officer','1999-10-10','MALE','Tanvir_Procurement_Node_319b2b44-8f1f-4bf5-bdaf-5f1afdf934c9.jpg',_binary '','2026-03-01','ENGLISH','1999261177654','P0088124','2026-06-22 03:01:41.003594',1,34,NULL);
/*!40000 ALTER TABLE `procurement_officers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27  3:17:47
