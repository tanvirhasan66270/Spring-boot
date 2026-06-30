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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','COMMERCIAL_OFFICER','CUSTOMER','DRIVER','LOGISTICS_OFFICER','MANAGER','PROCUREMENT','QC_INSPECTOR','SALES_OFFICER','SUPPLIER') DEFAULT NULL,
  `police_station_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`),
  KEY `FKipgo66qij29k0c5viibscm8y6` (`police_station_id`),
  CONSTRAINT `FKipgo66qij29k0c5viibscm8y6` FOREIGN KEY (`police_station_id`) REFERENCES `policestations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '\0','rafiq.driver2@gmail.com','Md. Rafiqul Islam','$2a$10$.5NbH3B8Z0FNqPlk1V5VSexLrV5VT0y18BHGFYj8zt31chrOqTwly','01812345688','DRIVER',NULL),(3,_binary '\0','rafiq.driver23@gmail.com','Md. Islam','$2a$10$9CrMdq6nNYiG4nqOMaegGuCslPxwip/o90oEFLdxFgYQSkJzssU/m','01812345658','DRIVER',NULL),(5,_binary '\0','rafiq.driver233@gmail.com','Md. Islam','$2a$10$zrwOsCnJbzf4TkrDm2PpquBlQheDZVqGgW6EDZBp3uI1cFK7ZKlaS','01812345558','DRIVER',NULL),(7,_binary '\0','srabonhasan66270@gmail.com','Md. Tanvir Hasan','$2a$10$A3.R6FaAHQhnvG1xljm0W.A1l7Y9KPg9MuUyABHZTacjlnYnYNykO','017123456788','CUSTOMER',NULL),(11,_binary '\0','srabonhasan662702@gmail.com','Md.Hasan','$2a$10$ur8i46A7Sri7nQFxzQyJwOXc9XR7Ab0h.e5bdxbvV9MkatqqqcZPO','0171234567828','CUSTOMER',NULL),(12,_binary '\0','srabonhasan66272@gmail.com','Md.Hasan','$2a$10$qN94Yt6VtM8IeRXVT88HE.W8jQUCZyfPQAg.jKgUF./deYw3Jy7u.','01712345628','CUSTOMER',NULL),(13,_binary '\0','srabonhasan6622@gmail.com','Md.Hasan','$2a$10$M9nUo/6N.nSxyNxoc8RzeeY/n31wC5IpZ/EOhg0VElbtxzHK9Z8la','017123456228','CUSTOMER',11),(16,_binary '','tanvirhasan70@gmail.com','Tanvir Rahman','123456','01712345678','COMMERCIAL_OFFICER',NULL),(18,_binary '','tanvirhasan0@gmail.com','Tanvir islam','$2a$10$88S7oJE0KPa18Zkz3vfiQOVT9t69WlonzRZ0x0qXB6C810tpSAaR.','017123456728','DRIVER',4),(19,_binary '','tanvirhasan6@gmail.com','Tanvir ','$2a$10$0xCUcI.pzljslN4VAmN/8uRRdg1gNRHM7du8fP2F0SdISRGnjD.HS','017123456718','LOGISTICS_OFFICER',1),(20,_binary '\0','tanvirhasan6627@gmail.com','Tanvir sra','$2a$10$mKS4B5kWnSqWi1Amb8ZBKuMVM2Tmi6QR/LG4TZW5kw04.isKhyTEy','001712345678','MANAGER',2),(23,_binary '','tanvirhasan2@gmail.com','Tanvir sra','$2a$10$VqpK8mD8jE0dhUi5gICFiezQktbpUmOfkgcl8hHgjMocidAukjPE2','0712345678','MANAGER',2),(26,_binary '\0','tanvirhas@gmail.com','Robin','$2a$10$5lp3vB45f6g9mRA3suE86usztURWIlrGHyXm0aq2Y3NPKrKr8y.qO','017123456778','QC_INSPECTOR',6),(35,_binary '','tanvirhas66270@gmail.com','Tanvir Rahman','$2a$10$6EsSX3xUFjfgPUIA3fma4.qqrODwF8Mfe0yBC/iqN8zWz5ORCUMOG','017123456786','SALES_OFFICER',6);
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

-- Dump completed on 2026-06-30 19:16:51
