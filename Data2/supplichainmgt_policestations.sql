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
-- Table structure for table `policestations`
--

DROP TABLE IF EXISTS `policestations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `policestations` (
  `active` bit(1) DEFAULT NULL,
  `district_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `name_bn` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa8qg39gnjde9t8dc8m9a4qbsb` (`district_id`),
  CONSTRAINT `FKa8qg39gnjde9t8dc8m9a4qbsb` FOREIGN KEY (`district_id`) REFERENCES `districts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policestations`
--

LOCK TABLES `policestations` WRITE;
/*!40000 ALTER TABLE `policestations` DISABLE KEYS */;
INSERT INTO `policestations` VALUES (_binary '',1,1,'Mirpur','মিরপুর','1216'),(_binary '',1,2,'Uttara','উত্তরা','1230'),(_binary '',1,3,'Gulshan','গুলশান','1212'),(_binary '',2,5,'Tongi','টঙ্গী','1710'),(_binary '',3,6,'Siddhirganj','সিদ্ধিরগঞ্জ','1430'),(_binary '',3,7,'Fatmullah','ফতুল্লা','1400'),(_binary '',5,10,'Coxs Bazar Sadar','কক্সবাজার সদর','4700'),(_binary '',5,11,'Ukhiya','উখিয়া','4750'),(_binary '',7,13,'Boalia','বোয়ালিয়া','6000'),(_binary '',8,14,'Bogura Sadar','বগুড়া সদর','5800'),(_binary '',10,16,'Khulna Sadar','খুলনা সদর','9100'),(_binary '',12,18,'Satkhira Sadar','সাতক্ষীরা সদর','9400'),(_binary '',13,19,'Barishal Sadar','বরিশাল সদর','8200'),(_binary '',14,20,'Bhola Sadar','ভোলা সদর','8300'),(_binary '',15,21,'Patuakhali Sadar','পটুয়াখালী সদর','8600'),(_binary '',16,22,'Kotwali Sylhet','কোতোয়ালী সিলেট','3100'),(_binary '',17,23,'Sreemangal','শ্রীমঙ্গল','3210'),(_binary '',18,24,'Habiganj Sadar','হবিগঞ্জ সদর','3300'),(_binary '',19,25,'Rangpur Sadar','রংপুর সদর','5400'),(_binary '',20,26,'Dinajpur Sadar','দিনাজপুর সদর','5200'),(_binary '',21,27,'Gaibandha Sadar','গাইবান্ধা সদর','5700'),(_binary '',23,29,'Jamalpur Sadar','জামালপুর সদর','2000'),(_binary '',24,30,'Netrokona Sadar','নেত্রকোনা সদর','2400'),(_binary '',25,31,'Central LAPD','সেন্ট্রাল এলএপিডি','90001'),(_binary '',25,32,'Hollywood Precinct','হলিউড প্রিসিন্ট','90028'),(_binary '',27,34,'Northern San Diego','নর্দার্ন সান ডিয়েগো','92109'),(_binary '',30,36,'108th Precinct','১০৮তম প্রিসিন্ট','11101'),(_binary '',32,37,'Central Dallas','সেন্ট্রাল ডালাস','75201'),(_binary '',36,39,'Tampa Central','টাম্পা সেন্ট্রাল','33602'),(_binary '',37,40,'Bishopsgate Police Station','বিশপসগেট পুলিশ স্টেশন','EC2M 4NR'),(_binary '',38,41,'Charing Cross Station','চারিং ক্রস স্টেশন','WC2N 4PA'),(_binary '',39,42,'Greenwich Police Station','গ্রিনউইচ পুলিশ স্টেশন','SE10 8JA'),(_binary '',40,43,'Salford Precinct Station','সালফোর্ড প্রিসিন্ট স্টেশন','M6 7RE'),(_binary '',41,44,'Stretford Police Station','স্ট্রেফোর্ড পুলিশ স্টেশন','M32 8XJ'),(_binary '',42,45,'Bolton Central','বোল্টন সেন্ট্রাল','BL1 1NX'),(_binary '',43,46,'Stewart Street Station','স্টুয়ার্ট স্ট্রিট স্টেশন','G4 0UG'),(_binary '',44,47,'Gayfield Square Station','গেফিল্ড স্কয়ার স্টেশন','EH1 3NW'),(_binary '',45,48,'Queen Street Aberdeen','কুইন স্ট্রিট অ্যাবারডিন','AB10 1ZA'),(_binary '',46,49,'Park Street PS','পার্ক স্ট্রিট থানা','700016'),(_binary '',46,50,'Salt Lake PS','সল্টলেক থানা','700091'),(_binary '',47,51,'Howrah Central PS','হাওড়া সেন্ট্রাল থানা','711101'),(_binary '',48,52,'Darjeeling Sadar PS','দার্জিলিং সদর থানা','734101'),(_binary '',49,53,'Colaba PS','কোলাবা থানা','400005'),(_binary '',49,54,'Andheri PS','আন্ধেরি থানা','400053'),(_binary '',50,55,'Shivajinagar PS','শিবাজীনগর থানা','411005'),(_binary '',51,56,'Sadar Nagpur PS','সদর নাগপুর থানা','440001'),(_binary '',52,57,'Connaught Place PS','কনট প্লেস থানা','110001'),(_binary '',53,58,'Daryaganj PS','দরিয়াগঞ্জ থানা','110002'),(_binary '',54,59,'Dwarka Sector 23 PS','দ্বারকা সেক্টর ২৩ থানা','110077'),(_binary '',55,60,'Navrangpura PS','নবরংপুরা থানা','380009'),(_binary '',56,61,'Varachha PS','ভারচ্ছা থানা','395006'),(_binary '',57,62,'Pradyuman Nagar PS','প্রদ্যুমন নগর থানা','360001'),(_binary '',58,63,'Yuexiu Substation','ইউয়েক্সিও সাবস্টেশন','510030'),(_binary '',59,64,'Futian Precinct','ফুটিয়ান প্রিসিন্ট','518000'),(_binary '',60,65,'Guancheng Station','গুয়াংচেং স্টেশন','523000'),(_binary '',61,66,'Lujiazui Station','লুজিয়াজুই স্টেশন','200120'),(_binary '',62,67,'Xinzhuang Substation','শিনজুয়াং সাবস্টেশন','201100'),(_binary '',63,68,'Tianlin Station','তিয়ানলিন স্টেশন','200233'),(_binary '',64,69,'Xiacheng Station','শিয়াচেং স্টেশন','310006'),(_binary '',65,70,'Haishu Station','হাইশু স্টেশন','315000'),(_binary '',66,71,'Choucheng Station','চৌচেং স্টেশন','322000'),(_binary '',67,72,'Altstadt Inspek','আল্টস্ট্যাড ইন্সপেকশন','80331'),(_binary '',68,73,'Mitte Nbg Inspek','মিত্তে এনবিজি ইন্সপেকশন','90402'),(_binary '',69,74,'Innenstadt Augs','ইনেশট্যাড অগসবার্গ','86150'),(_binary '',70,75,'Innenstadt Koln','ইনেশট্যাড কোলন','50667'),(_binary '',71,76,'Stadtmitte Duss','স্ট্যাডমিট্টে ডুসেলডর্ফ','40210'),(_binary '',72,77,'Mitte Dort Inspek','মিত্তে ডর্টমুন্ড','44135'),(_binary '',73,78,'Abschnitt 32 Berlin','আবছনিট ৩২ বার্লিন','10117'),(_binary '',74,79,'Abschnitt 13 Pankow','আবছনিট ১৩ প্যাঙ্কো','13187'),(_binary '',75,80,'Abschnitt 21 Spandau','আবছনিট ২১ স্প্যান্ডাউ','13597'),(_binary '',76,81,'Shinjuku Station Koban','শিনজুকু স্টেশন কোবান','160-0022'),(_binary '',77,82,'Shibuya Koban','শিবুয়া কোবান','150-0002'),(_binary '',78,83,'Marunouchi Koban','মারুনৌচি কোবান','100-0005'),(_binary '',79,84,'Umeda Koban','উমেদা কোবান','530-0001'),(_binary '',80,85,'Namba Koban','নাম্বা কোবান','542-0076'),(_binary '',81,86,'Sakai Station Koban','সাকাই স্টেশন কোবান','590-0971'),(_binary '',82,87,'Nagoya Station Koban','নাগোয়া স্টেশন কোবান','450-0002'),(_binary '',83,88,'Toyota Station Koban','টয়োটা স্টেশন কোবান','471-0025'),(_binary '',84,89,'Komaki Koban','কোমাকি কোবান','485-0041'),(_binary '',85,90,'52 Division TPS','৫২ ডিভিশন টিপিএস','M5V 2X4'),(_binary '',86,91,'Central Ottawa OPS','সেন্ট্রাল অটোয়া ওপিএস','K2P 1A4'),(_binary '',87,92,'11 Division PRP','১১ ডিভিশন পিআরপি','L5B 1B8'),(_binary '',88,93,'PDQ 20 Montreal','পিডিকিউ ২০ মন্ট্রিল','H3B 1X9'),(_binary '',89,94,'La Cite Station','লা সিতে স্টেশন','G1K 2L3'),(_binary '',90,95,'Chomedey Police','চোমেডি পুলিশ','H7V 1B2'),(_binary '',91,96,'Downtown VPD','ডাউনটাউন ভিপিডি','V6A 1S1'),(_binary '',92,97,'VicPD Headquarters','ভিকপিডি হেডকোয়ার্টার্স','V8W 1H9'),(_binary '',93,98,'Burnaby RCMP Detach','বার্নাবি আরসিএমপি','V5G 2T6'),(_binary '',94,99,'Sydney City Police','সিডনি সিটি পুলিশ','2000');
/*!40000 ALTER TABLE `policestations` ENABLE KEYS */;
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
