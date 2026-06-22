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
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK41g4n0emuvcm3qyf1f6cn43c0` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Woven Fabrics','All types of woven cloths including denim, twill, and poplin.'),(2,'Knit Fabrics','Knitted materials like single jersey, rib, and interlock fabric.'),(3,'Denim Materials','Heavyweight cotton warp-faced denim fabrics for jeans production.'),(4,'Synthetic Fabrics','Polyester, nylon, and spandex blended raw materials.'),(5,'Organic Cotton','100% certified eco-friendly organic cotton materials.'),(6,'Cotton Yarn','Raw spun cotton fibers used for knitting and weaving.'),(7,'Sewing Thread','High-tensile sewing threads for garment assembly lines.'),(8,'Polyester Thread','Synthetic polyester threads for heavy duty and outdoor gear.'),(9,'Embroidery Yarn','Specialized decorative yarns for computerized embroidery.'),(10,'Buttons','Plastic, metal, wooden, and snap buttons for apparel.'),(11,'Zippers','Nylon, metal, and plastic tooth zippers including sliders.'),(12,'Sew-in Labels','Brand tags, care labels, and size tabs for necklines.'),(13,'Hang Tags','Price and branding cards attached to finished clothing.'),(14,'Elastic Bands','Stretchable elastic components for waistbands and cuffs.'),(15,'Rivets & Eyelets','Metal reinforcement components for pocket corners.'),(16,'Velcro Tapes','Hook-and-loop fasteners used in activewear and jackets.'),(17,'Poly Bags','LDPE/PP protective plastic bags for individual clothing item packing.'),(18,'Corrugated Cartons','Heavy-duty master cartons for export cargo shipments.'),(19,'Packaging Tapes','Adhesive tapes for sealing export and storage boxes.'),(20,'Barcoded Labels','Thermal sticker labels for tracking cartons and inventory items.'),(21,'Desiccant Silica Gel','Moisture absorbing packets placed inside export cartons.'),(22,'Textile Dyes','Chemical dyes for fabric coloring and processing.'),(23,'Fixing Agents','Chemicals used to bind colors permanently to the fibers.'),(24,'Fabric Softeners','Finishing chemicals used to enhance textile hand-feel.'),(25,'Industrial Bleach','Whitening and preprocessing chemical compounds.'),(26,'Sewing Machine Spares','Needles, bobbins, loopers, and motors for sewing lines.'),(27,'Cutting Blades','Rotary and straight knives for fabric cutting section.'),(28,'Lubricants & Oils','Industrial grade oils for machinery maintenance.'),(29,'Pneumatic Components','Valves and cylinders used in automated textile machines.'),(30,'Plastic Pallets','Heavy duty platforms used for stacking goods via forklifts.'),(31,'Hand Pallet Jacks','Manual hydraulic trucks for moving cargo inside warehouse.'),(32,'Stretch Wraps','Industrial plastic wraps for securing palletized shipments.'),(33,'Safety Equipment (PPE)','Gloves, helmets, and high-visibility vests for warehouse staff.'),(34,'Barcode Scanners','Handheld laser scanners for inventory check-in/out.'),(35,'Thermal Printers','Label printers used for generating tracking barcodes.'),(36,'Office Stationery','Paper, pens, files, and general clerical supplies.'),(37,'RFID Tags','Radio-frequency identification tags for smart tracking.'),(38,'Mens Shirts','Finished woven formal and casual shirts for men.'),(39,'Mens Denim Trousers','Completed denim jeans ready for packing and export.'),(40,'Womens Dresses','Finished knitted and woven dresses for women.'),(41,'Kids Outerwear','Children jackets, sweaters, and protective wear.'),(42,'Activewear','Athletic and sportswear items like tracksuits and jerseys.'),(43,'GSM Cutters','Precision round fabric sample cutters for weight testing.'),(44,'PH Testing Strips','Reagents for chemical acidity check in fabric labs.'),(45,'Light Boxes','Color matching cabinets used under standardized lighting.'),(46,'Cleaning Chemicals','Detergents and sanitizers for factory floor maintenance.'),(47,'Maintenance Tools','Wrenches, screwdrivers, and basic engineering tools.'),(48,'Electrical Spares','Fuses, switches, and wiring components for power grids.'),(49,'Fire Safety Spares','Extinguisher refills and smoke detector sensors.'),(50,'Scrap Materials','Leftover fabric cut pieces and waste yarns sold externally.');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
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
