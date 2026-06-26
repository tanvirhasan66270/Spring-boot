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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `active` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `police_station_id` bigint DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','COMMERCIAL_OFFICER','CUSTOMER','DRIVER','LOGISTICS_OFFICER','MANAGER','PROCUREMENT','QC_INSPECTOR','SUPPLIER','SALES_OFFICER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`),
  KEY `FKipgo66qij29k0c5viibscm8y6` (`police_station_id`),
  CONSTRAINT `FKipgo66qij29k0c5viibscm8y6` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary '\0',1,NULL,'Password123','tanvirhasan66270@gmail.com','Al-Amin Hossain','+8801712345678','DRIVER'),(_binary '\0',8,NULL,'pa1234','isratjahaneya8@gmail.com','My Queen','+8801711223324','DRIVER'),(_binary '\0',10,NULL,'SecurePassword123','info@apextrims.com','Apex Trims Ltd','+8801711223344','SUPPLIER'),(_binary '\0',12,NULL,'ChemPass@2026','contact@anikchem.com','Anik Chemical Industries','+8801819556677','SUPPLIER'),(_binary '\0',14,NULL,'LogisticsMaster!','operation@bengallogistics.com','Bengal Global Logistics','+8801552334455','SUPPLIER'),(_binary '\0',15,NULL,'YarnSpinning@26','management@sufiayarn.com','Sufia Yarn Spinning Mill','+8801612778899','SUPPLIER'),(_binary '\0',16,NULL,'123456','srabonhasan66270@gmail.com','Md. Tanvir','+8801711223334','CUSTOMER'),(_binary '\0',18,NULL,'123456','farhana.yasmin@yahoo.com','Farhana Yasmin','+8801819665544','CUSTOMER'),(_binary '\0',22,NULL,'123456','tanvir.scm@outlook.com','Tanvir Ahmed','+8801914332211','CUSTOMER'),(_binary '\0',23,NULL,'123456','nusrat.jahan@gmail.com','Nusrat Jahan','+8801612445566','CUSTOMER'),(_binary '\0',28,NULL,'123456','arsa@gmail.com','Abdur Rahman','+8801552887766','CUSTOMER'),(_binary '\0',29,NULL,'SecureManagerPass12','kamal.manager@scmlogistics.com','Kamal Hossain','+8801755667788','MANAGER'),(_binary '\0',33,NULL,'123456','robin66270@gmail.com','Md Tanvir','+88017556657788','MANAGER'),(_binary '\0',34,NULL,'123456','robinhasan66270@gmail.com','Tanvir Procurement Node','+8801712009988','PROCUREMENT'),(_binary '\0',35,NULL,'123456','tanvir.commercial@scmlogistics.com','Tanvir Commercial Operations','+8801811443322','COMMERCIAL_OFFICER'),(_binary '\0',36,NULL,'123456','tanvir.sales@scmlogistics.com','Tanvir Sales Core','+8801711990022','SALES_OFFICER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27  3:17:53
