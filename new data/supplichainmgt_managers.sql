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
-- Table structure for table `managers`
--

DROP TABLE IF EXISTS `managers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `managers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` text,
  `created_at` datetime(6) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `dob` date NOT NULL,
  `gender` enum('FEMALE','MALE','OTHERS') DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `joining_date` date DEFAULT NULL,
  `language` enum('BANGLA','ENGLISH','OTHERS') DEFAULT NULL,
  `nid_number` varchar(50) NOT NULL,
  `passport_number` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcd7r3q5kikp4m9v0usn4il5sj` (`nid_number`),
  UNIQUE KEY `UK6sl3ig444d4qy4c2kq6ju96pf` (`user_id`),
  UNIQUE KEY `UKp7b7eu0xa932old2h8uht0wyh` (`passport_number`),
  KEY `FK5xctcstlt90o1rfh05nkbbwq5` (`police_station_id`),
  CONSTRAINT `FK5xctcstlt90o1rfh05nkbbwq5` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`),
  CONSTRAINT `FKsp1db43yf1nqhswrpbwmlnhb9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `managers`
--

LOCK TABLES `managers` WRITE;
/*!40000 ALTER TABLE `managers` DISABLE KEYS */;
INSERT INTO `managers` VALUES (1,'House 45, Sector 7, Uttara, Dhaka','2026-06-30 16:49:08.394393','General Manager','1999-10-10','MALE','uploads/manager/Tanvir_sra_d205b014-c2d2-4d63-813b-6711e2ac9fb6.JPG',_binary '','2026-07-01','BANGLA','1999261025999','B09876543','2026-06-30 16:58:40.095764',2,20),(4,'House 45, Sector 7, Uttara, Dhaka','2026-06-30 17:17:03.098114','General Manager','1999-10-10','MALE','uploads/manager/Tanvir_sra_7066b5fb-1923-4b24-9277-5faaa362000c.JPG',_binary '\0','2026-07-01','BANGLA','199921025999','B0986543','2026-06-30 17:31:47.286945',2,23);
/*!40000 ALTER TABLE `managers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 19:16:49
