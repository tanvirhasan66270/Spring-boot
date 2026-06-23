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
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `average_lead_time_days` int DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `rating` double DEFAULT '0',
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `police_station_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nid_number` varchar(255) DEFAULT NULL,
  `passport_number` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbchau2evg2tdp9dv3if1gblur` (`user_id`),
  UNIQUE KEY `UKq5uvp89ra4ksaty5ghyaw4kjr` (`email`),
  KEY `FKbq72j31c0nckxt7xfyjwojksh` (`police_station_id`),
  CONSTRAINT `FKbq72j31c0nckxt7xfyjwojksh` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`),
  CONSTRAINT `FKja3xaqihwgllahrmo5op9ks4d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (7,_binary '',4.5,'2026-06-20 01:20:41.090831',2,13,NULL,10,'Plot-45, Sector-7, Uttara, Dhaka','Md. Asaduzzaman','1992-05-15','info@apextrims.com','MALE','Apex_Trims_Ltd_d6579256-06de-4a92-b1bb-032d5e8fbfc4.jpg','Apex Trims Ltd','1992261547896','EA0987654','+8801711223344'),(12,_binary '',4.8,'2026-06-20 01:29:17.470217',3,24,NULL,12,'Bscic Industrial Area, Fatullah, Narayanganj','Anik Rahman','1988-11-20','contact@anikchem.com','MALE','Anik_Chemical_Industries_0e5a8379-a817-4a23-b9f4-8a97f0816d18.jpg','Anik Chemical Industries','1988159632478','EB0123456','+8801819556677'),(15,_binary '',4.7,'2026-06-20 01:30:00.669868',4,45,NULL,14,'Agrabad C/A, Double Mooring, Chittagong','Kamrul Hasan','1985-08-25','operation@bengallogistics.com','MALE','Bengal_Global_Logistics_15275887-2f68-4a7f-951c-23deefe68085.jpg','Bengal Global Logistics','1985159487623','EC0554433','+8801552334455'),(10,_binary '',4,'2026-06-20 01:30:50.892871',5,19,NULL,15,'Narsingdi Sadar, Narsingdi','Sufia Begum','1980-01-01','management@sufiayarn.com','FEMALE','Sufia_Yarn_Spinning_Mill_79638403-4836-4ea8-bcd5-f4c0208eca54.jpg','Sufia Yarn Spinning Mill','1980261594875','','+8801612778899');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-23  3:29:30
