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
-- Table structure for table `divisions`
--

DROP TABLE IF EXISTS `divisions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `divisions` (
  `active` bit(1) DEFAULT NULL,
  `country_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `name_bn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2o4cg3xxx0ea0mapwhjr7racp` (`country_id`),
  CONSTRAINT `FK2o4cg3xxx0ea0mapwhjr7racp` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `divisions`
--

LOCK TABLES `divisions` WRITE;
/*!40000 ALTER TABLE `divisions` DISABLE KEYS */;
INSERT INTO `divisions` VALUES (_binary '',1,1,'Dhaka','ঢাকা'),(_binary '',1,2,'Chattogram','চট্টগ্রাম'),(_binary '',1,3,'Rajshahi','রাজশাহী'),(_binary '',1,4,'Khulna','খুলনা'),(_binary '',1,5,'Barishal','বরিশাল'),(_binary '',1,6,'Sylhet','সিলেট'),(_binary '',1,7,'Rangpur','রংপুর'),(_binary '',1,8,'Mymensingh','ময়মনসিংহ'),(_binary '',2,9,'California','ক্যালিফোর্নিয়া'),(_binary '',2,10,'New York','নিউ ইয়র্ক'),(_binary '',2,11,'Texas','টেক্সাস'),(_binary '',2,12,'Florida','ফ্লোরিডা'),(_binary '',3,13,'Greater London','গ্রেটার লন্ডন'),(_binary '',3,14,'Greater Manchester','গ্রেটার ম্যানচেস্টার'),(_binary '',3,15,'Scotland','স্কটল্যান্ড'),(_binary '',4,16,'West Bengal','পশ্চিমবঙ্গ'),(_binary '',4,17,'Maharashtra','মহারাষ্ট্র'),(_binary '',4,18,'Delhi','দিল্লি'),(_binary '',4,19,'Gujarat','গুজরাট'),(_binary '',5,20,'Guangdong','গুয়াংডং'),(_binary '',5,21,'Shanghai','সাংহাই'),(_binary '',5,22,'Zhejiang','চচিয়াং'),(_binary '',6,23,'Bavaria','বাভারিয়া'),(_binary '',6,24,'North Rhine-Westphalia','নর্থ রাইন-ওয়েস্টফালিয়া'),(_binary '',6,25,'Berlin','বার্লিন'),(_binary '',7,26,'Tokyo','টোকিও'),(_binary '',7,27,'Osaka','ওসাকা'),(_binary '',7,28,'Aichi','আইচি'),(_binary '',8,29,'Ontario','অন্টারিও'),(_binary '',8,30,'Quebec','কুইবেক'),(_binary '',8,31,'British Columbia','ব্রিটিশ কলম্বিয়া'),(_binary '',9,32,'New South Wales','নিউ সাউথ ওয়েলস'),(_binary '',9,33,'Victoria','ভিক্টোরিয়া'),(_binary '',9,34,'Queensland','কুইন্সল্যান্ড'),(_binary '',10,35,'Dubai','দুবাই'),(_binary '',10,36,'Abu Dhabi','আবুধাবি'),(_binary '',10,37,'Sharjah','শারজাহ'),(_binary '',11,38,'Riyadh','রিয়াদ'),(_binary '',11,39,'Makkah','মক্কা'),(_binary '',11,40,'Eastern Province','পূর্বাঞ্চলীয় প্রদেশ'),(_binary '',12,41,'Central Region','কেন্দ্রীয় অঞ্চল'),(_binary '',12,42,'West Region','পশ্চিম অঞ্চল'),(_binary '',13,43,'Selangor','সেলাঙ্গর'),(_binary '',13,44,'Kuala Lumpur','কুয়ালালামপুর'),(_binary '',13,45,'Johor','জোহর'),(_binary '',14,46,'Istanbul','ইস্তাম্বুল'),(_binary '',14,47,'Ankara','আঙ্কারা'),(_binary '',14,48,'Izmir','ইজমির'),(_binary '',15,49,'Ile-de-France','ইল-দ্য-ফ্রান্স'),(_binary '',15,50,'Auvergne-Rhone-Alpes','ওভার্ন-রোন-আল্পস'),(_binary '',16,51,'Lombardy','লোম্বার্দি'),(_binary '',16,52,'Lazio','লাৎসিও'),(_binary '',17,53,'Seoul','সিউল'),(_binary '',17,54,'Gyeonggi','গিয়ংগি'),(_binary '',17,55,'Busan','বুসান'),(_binary '',18,56,'Ho Chi Minh City','হো চি মিন সিটি'),(_binary '',18,57,'Hanoi','হ্যানয়'),(_binary '',18,58,'Binh Duong','বিন দুওং'),(_binary '',19,59,'Sao Paulo','সাও পাওলো'),(_binary '',19,60,'Rio de Janeiro','রিও ডি জেনিরো'),(_binary '',20,61,'Gauteng','গাউতেং'),(_binary '',20,62,'Western Cape','ওয়েস্টার্ন কেপ');
/*!40000 ALTER TABLE `divisions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-21  4:25:17
