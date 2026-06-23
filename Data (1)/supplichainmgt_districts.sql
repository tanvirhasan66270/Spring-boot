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
-- Table structure for table `districts`
--

DROP TABLE IF EXISTS `districts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `districts` (
  `active` bit(1) DEFAULT NULL,
  `division_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `district_code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_bn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl374uao5cplc8w347pn93svoc` (`division_id`),
  CONSTRAINT `FKl374uao5cplc8w347pn93svoc` FOREIGN KEY (`division_id`) REFERENCES `divisions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `districts`
--

LOCK TABLES `districts` WRITE;
/*!40000 ALTER TABLE `districts` DISABLE KEYS */;
INSERT INTO `districts` VALUES (_binary '',1,1,'BD-DH-01','Dhaka City','ঢাকা সিটি'),(_binary '',1,2,'BD-DH-02','Gazipur','গাজীপুর'),(_binary '',1,3,'BD-DH-03','Narayanganj','নারায়ণগঞ্জ'),(_binary '',2,4,'BD-CH-01','Chattogram City','চট্টগ্রাম সিটি'),(_binary '',2,5,'BD-CH-02','Cox\'s Bazar','কক্সবাজার'),(_binary '',2,6,'BD-CH-03','Cumilla','কুমিল্লা'),(_binary '',3,7,'BD-RJ-01','Rajshahi City','রাজশাহী সিটি'),(_binary '',3,8,'BD-RJ-02','Bogura','বগুড়া'),(_binary '',3,9,'BD-RJ-03','Pabna','পাবনা'),(_binary '',4,10,'BD-KH-01','Khulna City','খুলনা সিটি'),(_binary '',4,11,'BD-KH-02','Jashore','যশোর'),(_binary '',4,12,'BD-KH-03','Satkhira','সাতক্ষীরা'),(_binary '',5,13,'BD-BA-01','Barishal City','বরিশাল সিটি'),(_binary '',5,14,'BD-BA-02','Bhola','ভোলা'),(_binary '',5,15,'BD-BA-03','Patuakhali','পটুয়াখালী'),(_binary '',6,16,'BD-SY-01','Sylhet City','সিলেট সিটি'),(_binary '',6,17,'BD-SY-02','Moulvibazar','مৌলভীবাজার'),(_binary '',6,18,'BD-SY-03','Habiganj','হবিগঞ্জ'),(_binary '',7,19,'BD-RP-01','Rangpur City','রংপুর সিটি'),(_binary '',7,20,'BD-RP-02','Dinajpur','দিনাজপুর'),(_binary '',7,21,'BD-RP-03','Gaibandha','গাইবান্ধা'),(_binary '',8,22,'BD-MY-01','Mymensingh City','ময়মনসিংহ সিটি'),(_binary '',8,23,'BD-MY-02','Jamalpur','জামালপুর'),(_binary '',8,24,'BD-MY-03','Netrokona','নেত্রকোনা'),(_binary '',9,25,'US-CA-01','Los Angeles','লস অ্যাঞ্জেলেস'),(_binary '',9,26,'US-CA-02','San Francisco','সান ফ্রান্সিসকো'),(_binary '',9,27,'US-CA-03','San Diego','সান ডিয়েগো'),(_binary '',10,28,'US-NY-01','Manhattan','ম্যানهاটন'),(_binary '',10,29,'US-NY-02','Brooklyn','ব্রুকলিন'),(_binary '',10,30,'US-NY-03','Queens','কুইন্স'),(_binary '',11,31,'US-TX-01','Houston','হিউস্টন'),(_binary '',11,32,'US-TX-02','Dallas','ডালাস'),(_binary '',11,33,'US-TX-03','Austin','অস্টিন'),(_binary '',12,34,'US-FL-01','Miami','মায়ামি'),(_binary '',12,35,'US-FL-02','Orlando','অরল্যান্ডো'),(_binary '',12,36,'US-FL-03','Tampa','টাম্পা'),(_binary '',13,37,'UK-LN-01','City of London','সিটি অফ লন্ডন'),(_binary '',13,38,'UK-LN-02','Westminster','ওয়েস্টমিনস্টার'),(_binary '',13,39,'UK-LN-03','Greenwich','গ্রিনউইচ'),(_binary '',14,40,'UK-MN-01','Salford','সালফোর্ড'),(_binary '',14,41,'UK-MN-02','Trafford','ট্রাফোর্ড'),(_binary '',14,42,'UK-MN-03','Bolton','বোল্টন'),(_binary '',15,43,'UK-SC-01','Glasgow','গ্লাসগো'),(_binary '',15,44,'UK-SC-02','Edinburgh','এডিসবার্গের'),(_binary '',15,45,'UK-SC-03','Aberdeen','অ্যাবারডিন'),(_binary '',16,46,'IN-WB-01','Kolkata','কলকাতা'),(_binary '',16,47,'IN-WB-02','Howrah','হাওড়া'),(_binary '',16,48,'IN-WB-03','Darjeeling','দার্জিলিং'),(_binary '',17,49,'IN-MH-01','Mumbai','মুম্বাই'),(_binary '',17,50,'IN-MH-02','Pune','পুনে'),(_binary '',17,51,'IN-MH-03','Nagpur','নাгপুর'),(_binary '',18,52,'IN-DL-01','New Delhi','নয়াদিল্লি'),(_binary '',18,53,'IN-DL-02','Central Delhi','সেন্ট্রাল দিল্লি'),(_binary '',18,54,'IN-DL-03','Dwarka','দ্বারকা'),(_binary '',19,55,'IN-GJ-01','Ahmedabad','আহমেদাবাদ'),(_binary '',19,56,'IN-GJ-02','Surat','সুরাট'),(_binary '',19,57,'IN-GJ-03','Rajkot','রাজকোট'),(_binary '',20,58,'CN-GD-01','Guangzhou','गुয়াংজু'),(_binary '',20,59,'CN-GD-02','Shenzhen','শেনজেন'),(_binary '',20,60,'CN-GD-03','Dongguan','দংগুয়ান'),(_binary '',21,61,'CN-SH-01','Pudong','পুডং'),(_binary '',21,62,'CN-SH-02','Minhang','মিনহাং'),(_binary '',21,63,'CN-SH-03','Xuhui','শুহুই'),(_binary '',22,64,'CN-ZJ-01','Hangzhou','হাংজু'),(_binary '',22,65,'CN-ZJ-02','Ningbo','নিংবো'),(_binary '',22,66,'CN-ZJ-03','Yiwu','ইইউ'),(_binary '',23,67,'DE-BY-01','Munich','মিউনিখ'),(_binary '',23,68,'DE-BY-02','Nuremberg','নুরেমবার্গ'),(_binary '',23,69,'DE-BY-03','Augsburg','আউগসবুর্গ'),(_binary '',24,70,'DE-NW-01','Cologne','কোলন'),(_binary '',24,71,'DE-NW-02','Dusseldorf','ডুসেলডর্ফ'),(_binary '',24,72,'DE-NW-03','Dortmund','ডর্টমুন্ড'),(_binary '',25,73,'DE-BE-01','Mitte','মিত্তে'),(_binary '',25,74,'DE-BE-02','Pankow','প্যাঙ্কো'),(_binary '',25,75,'DE-BE-03','Spandau','স্প্যান্ডাউ'),(_binary '',26,76,'JP-TK-01','Shinjuku','শিনজুকু'),(_binary '',26,77,'JP-TK-02','Shibuya','শিবুয়া'),(_binary '',26,78,'JP-TK-03','Chiyoda','চিয়োদা'),(_binary '',27,79,'JP-OS-01','Kita','কিতা'),(_binary '',27,80,'JP-OS-02','Chuo','চুও'),(_binary '',27,81,'JP-OS-03','Sakai','সাকাই'),(_binary '',28,82,'JP-AI-01','Nagoya','নাগোয়া'),(_binary '',28,83,'JP-AI-02','Toyota City','টয়োটা সিটি'),(_binary '',28,84,'JP-AI-03','Komaki','কোমাকি'),(_binary '',29,85,'CA-ON-01','Toronto','টরন্টো'),(_binary '',29,86,'CA-ON-02','Ottawa','অটোয়া'),(_binary '',29,87,'CA-ON-03','Mississauga','মিসিসাগা'),(_binary '',30,88,'CA-QC-01','Montreal','মন্ট্রিল'),(_binary '',30,89,'CA-QC-02','Quebec City','কুইবেক সিটি'),(_binary '',30,90,'CA-QC-03','Laval','লাভাল'),(_binary '',31,91,'CA-BC-01','Vancouver','ভ্যাঙ্কুভার'),(_binary '',31,92,'CA-BC-02','Victoria City','ভিক্টোরিয়া সিটি'),(_binary '',31,93,'CA-BC-03','Burnaby','বার্নাবি'),(_binary '',32,94,'AU-NSW-01','Sydney','সিডনি');
/*!40000 ALTER TABLE `districts` ENABLE KEYS */;
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
