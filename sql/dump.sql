-- MariaDB dump 10.19  Distrib 10.11.6-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: taxi_partner
-- ------------------------------------------------------
-- Server version	10.11.6-MariaDB-0+deb12u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `collaboration_terms`
--

DROP TABLE IF EXISTS `collaboration_terms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collaboration_terms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) NOT NULL,
  `term_name` varchar(100) NOT NULL,
  `term_value` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaboration_terms`
--

LOCK TABLES `collaboration_terms` WRITE;
/*!40000 ALTER TABLE `collaboration_terms` DISABLE KEYS */;
INSERT INTO `collaboration_terms` VALUES
(20,'1','percentTurnover','40'),
(21,'1','cardCommission','3'),
(22,'1','partnerCommission','20'),
(23,'1','boltCommission','20'),
(24,'1','settlementLimit','200'),
(61,'2','percentTurnover','40'),
(62,'2','fuelCost','0'),
(63,'2','cardCommission','3.5'),
(64,'2','partnerCommission','20'),
(65,'2','boltCommission','20'),
(66,'2','settlementLimit','500'),
(67,'1','fuelCost','0'),
(68,'3','percentTurnover','40'),
(69,'3','fuelCost','0'),
(70,'3','cardCommission','3'),
(71,'3','partnerCommission','20'),
(72,'3','boltCommission','20'),
(73,'3','settlementLimit','500'),
(80,'T14','percentTurnover','40'),
(81,'T14','fuelCost','0'),
(82,'T14','cardCommission','3'),
(83,'T14','partnerCommission','20'),
(84,'T14','boltCommission','20'),
(85,'T14','settlementLimit','1000'),
(86,'T1','percentTurnover','80'),
(87,'T1','fuelCost','0'),
(88,'T1','cardCommission','100'),
(89,'T1','partnerCommission','10'),
(90,'T1','boltCommission','0'),
(91,'T1','settlementLimit','20000'),
(92,'10','percentTurnover','0'),
(93,'10','fuelCost','0'),
(94,'10','cardCommission','0'),
(95,'10','partnerCommission','0'),
(96,'10','boltCommission','0'),
(97,'10','settlementLimit','0'),
(110,'T66','percentTurnover','20.1'),
(111,'T66','fuelCost','0'),
(112,'T66','cardCommission','90.1'),
(113,'T66','partnerCommission','10'),
(114,'T66','boltCommission','10.98'),
(115,'T66','settlementLimit','500.8');
/*!40000 ALTER TABLE `collaboration_terms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drivers`
--

DROP TABLE IF EXISTS `drivers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drivers` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `balance` float DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drivers`
--

LOCK TABLES `drivers` WRITE;
/*!40000 ALTER TABLE `drivers` DISABLE KEYS */;
INSERT INTO `drivers` VALUES
('T14','Jan Kowalski','pass123',0,'2025-02-18 16:15:16');
/*!40000 ALTER TABLE `drivers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drivers_backup`
--

DROP TABLE IF EXISTS `drivers_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drivers_backup` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `balance` float DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drivers_backup`
--

LOCK TABLES `drivers_backup` WRITE;
/*!40000 ALTER TABLE `drivers_backup` DISABLE KEYS */;
INSERT INTO `drivers_backup` VALUES
('T14','Jan Kowalski','pass123',0,'2025-02-18 16:15:16');
/*!40000 ALTER TABLE `drivers_backup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historia_salda`
--

DROP TABLE IF EXISTS `historia_salda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historia_salda` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kierowca_id` varchar(50) NOT NULL,
  `zmiana` decimal(10,2) NOT NULL,
  `saldo_po` decimal(10,2) NOT NULL,
  `powod` varchar(100) NOT NULL,
  `opis` text DEFAULT NULL,
  `data` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historia_salda`
--

LOCK TABLES `historia_salda` WRITE;
/*!40000 ALTER TABLE `historia_salda` DISABLE KEYS */;
INSERT INTO `historia_salda` VALUES
(1,'10',-50.00,50.00,'rozliczenie',NULL,'2025-03-29 19:38:10'),
(2,'10',200.00,250.00,'rozliczenie',NULL,'2025-03-29 19:38:49'),
(3,'10',-100.00,150.00,'rozliczenie',NULL,'2025-03-29 19:39:05'),
(5,'10',100.00,250.00,'inny',NULL,'2025-03-29 19:52:57'),
(6,'10',100.00,350.00,'inny',NULL,'2025-03-29 19:53:58'),
(7,'T50',200.00,1200.00,'kara',NULL,'2025-04-04 15:54:44'),
(8,'T1',100.00,1100.00,'nagrodówka',NULL,'2025-04-04 15:55:39'),
(9,'9',100.00,200.00,'nagrodówka',NULL,'2025-04-04 15:56:00'),
(10,'T52',-300.00,-78.00,'karownia',NULL,'2025-04-04 16:02:15'),
(11,'4',20.00,120.00,'Premia',NULL,'2025-04-06 10:40:39'),
(12,'1',-100.00,197.00,'nie tak',NULL,'2025-04-06 11:02:37'),
(13,'1',230.00,187.00,'Premia',NULL,'2025-04-06 11:06:55'),
(14,'1',100.00,287.00,'Premia',NULL,'2025-04-06 11:08:25'),
(15,'T4',100.00,100.00,'Premia',NULL,'2025-04-26 17:52:21'),
(16,'T4',100.00,255.20,'Premia',NULL,'2025-04-27 13:01:52'),
(17,'T4',50.00,344.00,'Premia',NULL,'2025-04-27 13:14:51'),
(18,'T4',-378.92,0.00,'Rozliczenie',NULL,'2025-04-27 13:44:14'),
(19,'T4',-75.00,0.00,'Rozliczenie',NULL,'2025-04-27 16:38:02'),
(20,'T4',-50.00,92.50,'Kara',NULL,'2025-04-27 17:35:19'),
(21,'T14',50.00,96.56,'Premia',NULL,'2025-04-27 20:20:40'),
(22,'T4',120.00,335.17,'Premia',NULL,'2025-04-27 20:48:07'),
(23,'T4',-100.00,277.45,'bo tak',NULL,'2025-04-28 16:46:29'),
(24,'T1',-1200.00,8853.18,'Kara',NULL,'2025-05-07 10:36:26'),
(25,'T1',125.00,8978.18,'Premia',NULL,'2025-05-07 10:37:09'),
(26,'T14',-50.00,47.04,'Kara',NULL,'2025-05-09 11:38:13'),
(27,'T14',500.00,650.80,'Kara',NULL,'2025-05-12 13:27:16'),
(28,'T14',-650.80,0.00,'Rozliczenie',NULL,'2025-05-12 13:28:14'),
(29,'T14',-340.00,-403.20,'kom',NULL,'2025-05-13 10:23:37'),
(30,'T14',500.00,200.92,'Premia',NULL,'2025-05-13 12:13:46'),
(31,'T14',-100.00,100.92,'Kara',NULL,'2025-05-13 12:14:31'),
(32,'T66',200.78,300.98,'Rozliczenie',NULL,'2025-06-17 14:53:06'),
(33,'T66',10.89,311.87,'Rozliczenie',NULL,'2025-06-17 15:07:39'),
(34,'T66',-11.87,300.00,'Rozliczenie',NULL,'2025-06-17 15:07:54');
/*!40000 ALTER TABLE `historia_salda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inwentaryzacje`
--

DROP TABLE IF EXISTS `inwentaryzacje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inwentaryzacje` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rejestracja` varchar(20) NOT NULL,
  `przebieg` int(11) NOT NULL,
  `czyste_wewnatrz` tinyint(1) DEFAULT 1,
  `photo_front` varchar(255) DEFAULT NULL,
  `data_dodania` datetime DEFAULT current_timestamp(),
  `photo_back` varchar(255) DEFAULT NULL,
  `photo_left` varchar(255) DEFAULT NULL,
  `photo_right` varchar(255) DEFAULT NULL,
  `kamizelki_qty` tinyint(4) DEFAULT NULL,
  `photo_dirt1` varchar(255) DEFAULT NULL,
  `photo_dirt2` varchar(255) DEFAULT NULL,
  `photo_dirt3` varchar(255) DEFAULT NULL,
  `photo_dirt4` varchar(255) DEFAULT NULL,
  `licencja` tinyint(1) NOT NULL DEFAULT 0,
  `legalizacja` tinyint(1) NOT NULL DEFAULT 0,
  `dowod` tinyint(1) NOT NULL DEFAULT 0,
  `ubezpieczenie` tinyint(1) NOT NULL DEFAULT 0,
  `karta_lotniskowa` tinyint(1) NOT NULL DEFAULT 0,
  `gasnica` tinyint(1) NOT NULL DEFAULT 0,
  `lewarek` tinyint(1) NOT NULL DEFAULT 0,
  `trojkat` tinyint(1) NOT NULL DEFAULT 0,
  `kamizelka` tinyint(1) NOT NULL DEFAULT 0,
  `uwagi` text DEFAULT NULL,
  `kierowca_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inwentaryzacje`
--

LOCK TABLES `inwentaryzacje` WRITE;
/*!40000 ALTER TABLE `inwentaryzacje` DISABLE KEYS */;
INSERT INTO `inwentaryzacje` VALUES
(1,'KR1234X',128444,0,'uploads/inventory/front_67f7f8c7debd7.jpg','2025-04-16 17:16:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(2,'KR1234X',1,1,'uploads/inventory/front_68078731e50bf.jpg','2025-04-22 14:10:25',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(3,'KR1234X',123456,0,'uploads/inventory/front_68078d75539e2.jpg','2025-04-22 14:37:09',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(4,'KR1234X',128555,0,'uploads/inventory/front_68078e8ae328b.jpg','2025-04-22 14:41:46',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(5,'KR1234X',111111,0,'uploads/inventory/front_6807941a546ea.jpg','2025-04-22 15:05:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(6,'KR1234X',123456,0,'uploads/inventory/front_6807a439863c1.jpg','2025-04-22 16:14:17',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(7,'KR1234X',123456,0,'uploads/inventory/front_6807a5339872f.jpg','2025-04-22 16:18:27',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(8,'KR1234X',123456,0,'uploads/inventory/front_6807a59228d07.jpg','2025-04-22 16:20:02',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(9,'KR1234X',129800,1,'uploads/inventory/front_6807a6262bc02.jpg','2025-04-22 16:22:30',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(10,'KR1234X',999991,1,'uploads/inventory/front_6807a73ec610a.jpg','2025-04-22 16:27:10',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(11,'KR1234X',128111,0,'uploads/inventory/front_680a281ca573e.jpg','2025-04-24 14:01:32','uploads/inventory/back_680a281ca7aa1.jpg','uploads/inventory/left_680a281ca9df3.jpg','uploads/inventory/right_680a281cac26e.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a281cae82b.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(12,'KR1234X',128111,0,'uploads/inventory/front_680a28302bb8a.jpg','2025-04-24 14:01:52','uploads/inventory/back_680a28302ded6.jpg','uploads/inventory/left_680a283030289.jpg','uploads/inventory/right_680a28303263e.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a283034904.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(13,'KR1234X',128111,0,'uploads/inventory/front_680a2833d4d78.jpg','2025-04-24 14:01:55','uploads/inventory/back_680a2833d6dec.jpg','uploads/inventory/left_680a2833d8eb7.jpg','uploads/inventory/right_680a2833db053.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a2833dd20e.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(14,'KR1234X',128111,0,'uploads/inventory/front_680a283e204ac.jpg','2025-04-24 14:02:06','uploads/inventory/back_680a283e22a22.jpg','uploads/inventory/left_680a283e24edf.jpg','uploads/inventory/right_680a283e271d7.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a283e29328.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(15,'KR1234X',128111,0,'uploads/inventory/front_680a28402d66d.jpg','2025-04-24 14:02:08','uploads/inventory/back_680a28402f9b3.jpg','uploads/inventory/left_680a284031e24.jpg','uploads/inventory/right_680a28403435c.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a284036841.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(16,'KR1234X',128111,0,'uploads/inventory/front_680a28643319d.jpg','2025-04-24 14:02:44','uploads/inventory/back_680a2864353b9.jpg','uploads/inventory/left_680a2864375fe.jpg','uploads/inventory/right_680a2864397c6.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a28643b9a0.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(17,'KR1234X',128111,0,'uploads/inventory/front_680a2864727bd.jpg','2025-04-24 14:02:44','uploads/inventory/back_680a286474884.jpg','uploads/inventory/left_680a2864769c6.jpg','uploads/inventory/right_680a286478aef.jpg',2,NULL,NULL,'uploads/inventory/dirt3_680a28647acdd.jpg',NULL,1,0,1,0,1,1,0,0,1,'ffdsssdfgg','0'),
(18,'KR1234X',128500,0,'uploads/inventory/front_680a2b9b5512f.jpg','2025-04-24 14:16:27','uploads/inventory/back_680a2b9b57265.jpg','uploads/inventory/left_680a2b9b5947f.jpg','uploads/inventory/right_680a2b9b5b22b.jpg',0,NULL,'uploads/inventory/dirt2_680a2b9b5cf86.jpg',NULL,NULL,1,0,0,0,0,0,0,0,0,'gggbb','0'),
(19,'KR1234X',128555,0,'uploads/inventory/front_680a2df32565c.jpg','2025-04-24 14:26:27','uploads/inventory/back_680a2df329746.jpg','uploads/inventory/left_680a2df32cf09.jpg','uploads/inventory/right_680a2df32f0c6.jpg',0,'uploads/inventory/dirt1_680a2df330e42.jpg',NULL,NULL,NULL,0,1,0,0,0,0,0,0,0,'ryccvv','0'),
(20,'KR1234X',128777,0,'uploads/inventory/front_680a347d68e8b.jpg','2025-04-24 14:54:21','uploads/inventory/back_680a347d69182.jpg','uploads/inventory/left_680a347d693f5.jpg','uploads/inventory/right_680a347d6961c.jpg',0,'uploads/inventory/dirt1_680a347d698cf.jpg',NULL,NULL,NULL,0,0,0,0,0,1,1,0,0,'ffdcccc','0'),
(21,'KR1234X',128541,0,'uploads/inventory/front_680a3a7aafec2.jpg','2025-04-24 15:19:54','uploads/inventory/back_680a3a7ab01dd.jpg','uploads/inventory/left_680a3a7ab048a.jpg','uploads/inventory/right_680a3a7ab06d3.jpg',0,'uploads/inventory/dirt1_680a3a7ab0904.jpg','uploads/inventory/dirt2_680a3a7ab0bdc.jpg','uploads/inventory/dirt3_680a3a7ab0e56.jpg','uploads/inventory/dirt4_680a3a7ab10e2.jpg',0,0,0,1,0,1,0,1,0,NULL,'0'),
(22,'KR1234X',128541,1,'uploads/inventory/front_680a3aa43dda4.jpg','2025-04-24 15:20:36','uploads/inventory/back_680a3aa43e0b2.jpg','uploads/inventory/left_680a3aa43e305.jpg','uploads/inventory/right_680a3aa43e58b.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'0'),
(23,'KR1234X',128508,1,'uploads/inventory/front_680a3e54e8d80.jpg','2025-04-24 15:36:20','uploads/inventory/back_680a3e54e9071.jpg','uploads/inventory/left_680a3e54e9300.jpg','uploads/inventory/right_680a3e54e9571.jpg',8,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,1,NULL,'0'),
(24,'KR1234X',132000,0,'uploads/inventory/front_680cc46cc5183.jpg','2025-04-26 13:33:00','uploads/inventory/back_680cc46cc54d5.jpg','uploads/inventory/left_680cc46cc574d.jpg','uploads/inventory/right_680cc46cc59bd.jpg',4,'uploads/inventory/dirt1_680cc46cc5c22.jpg',NULL,NULL,NULL,1,0,0,1,0,0,1,0,1,'czesc','0'),
(25,'TEST123',1500,1,NULL,'2025-04-26 14:30:51',NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,1,0,1,1,0,1,1,1,1,'Testowy rekord z JWT','1'),
(26,'KR1234X',135003,1,'uploads/inventory/front_680cddeb774cb.jpg','2025-04-26 15:21:47','uploads/inventory/back_680cddeb77707.jpg','uploads/inventory/left_680cddeb7794a.jpg','uploads/inventory/right_680cddeb77b56.jpg',0,NULL,NULL,NULL,NULL,0,0,0,1,0,1,0,1,0,NULL,'1'),
(27,'KR1234X',135008,1,'uploads/inventory/front_680ce26fbf1e2.jpg','2025-04-26 15:41:03','uploads/inventory/back_680ce26fbf468.jpg','uploads/inventory/left_680ce26fbf6b6.jpg','uploads/inventory/right_680ce26fbf893.jpg',2,NULL,NULL,NULL,NULL,0,0,0,0,1,0,1,0,1,NULL,'2'),
(28,'KR1234X',135101,1,'uploads/inventory/front_680ce55553841.jpg','2025-04-26 15:53:25','uploads/inventory/back_680ce55553aa9.jpg','uploads/inventory/left_680ce55553c9f.jpg','uploads/inventory/right_680ce55553e79.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,1,0,1,0,NULL,'0'),
(29,'KR1234X',135102,1,'uploads/inventory/front_680ce8bc5346d.jpg','2025-04-26 16:07:56','uploads/inventory/back_680ce8bc53769.jpg','uploads/inventory/left_680ce8bc539c4.jpg','uploads/inventory/right_680ce8bc53bec.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'T4'),
(30,'KR1234X',135109,0,'uploads/inventory/front_680cfd401f7da.jpg','2025-04-26 17:35:28','uploads/inventory/back_680cfd401fb78.jpg','uploads/inventory/left_680cfd401fe44.jpg','uploads/inventory/right_680cfd4020129.jpg',4,'uploads/inventory/dirt1_680cfd4020450.jpg',NULL,NULL,NULL,1,1,1,1,1,0,1,0,1,'taktakgfxddd','T4'),
(31,'DW1234X',50001,1,'uploads/inventory/front_680d0028a9a84.jpg','2025-04-26 17:47:52','uploads/inventory/back_680d0028a9cab.jpg','uploads/inventory/left_680d0028a9e6c.jpg','uploads/inventory/right_680d0028aa0cf.jpg',0,NULL,NULL,NULL,NULL,0,0,0,1,1,1,0,0,0,'tak','T14'),
(32,'KR1234X',135110,1,'uploads/inventory/front_680d0cdac6194.jpg','2025-04-26 18:42:02','uploads/inventory/back_680d0cdac6537.jpg','uploads/inventory/left_680d0cdac67d3.jpg','uploads/inventory/right_680d0cdac6a71.jpg',0,NULL,NULL,NULL,NULL,1,1,1,0,1,0,0,0,0,NULL,'T4'),
(33,'KR1234X',135113,1,'uploads/inventory/front_680d110446018.jpg','2025-04-26 18:59:48','uploads/inventory/back_680d110446386.jpg','uploads/inventory/left_680d1104466b5.jpg','uploads/inventory/right_680d110446986.jpg',0,NULL,NULL,NULL,NULL,1,1,1,0,0,1,1,0,0,NULL,'T4'),
(34,'KR1234X',135117,1,'uploads/inventory/front_680d136bebbea.jpg','2025-04-26 19:10:03','uploads/inventory/back_680d136bebf49.jpg','uploads/inventory/left_680d136bec221.jpg','uploads/inventory/right_680d136bec523.jpg',0,NULL,NULL,NULL,NULL,1,0,0,1,1,0,1,0,0,NULL,'T14'),
(35,'DW1234X',50002,1,'uploads/inventory/front_680d18f93b4f9.jpg','2025-04-26 19:33:45','uploads/inventory/back_680d18f93b922.jpg','uploads/inventory/left_680d18f93bc8f.jpg','uploads/inventory/right_680d18f93bfa2.jpg',0,NULL,NULL,NULL,NULL,0,0,1,1,1,0,1,0,0,NULL,'T14'),
(36,'KR1234X',137000,1,'uploads/inventory/front_680d1ea87135f.jpg','2025-04-26 19:58:00','uploads/inventory/back_680d1ea871739.jpg','uploads/inventory/left_680d1ea8719dd.jpg','uploads/inventory/right_680d1ea871c53.jpg',0,NULL,NULL,NULL,NULL,0,0,0,1,0,1,0,0,0,NULL,'T4'),
(37,'KR1234X',151333,1,'uploads/inventory/front_680e6fc350e19.jpg','2025-04-27 19:56:19','uploads/inventory/back_680e6fc351101.jpg','uploads/inventory/left_680e6fc351320.jpg','uploads/inventory/right_680e6fc3514e2.jpg',0,NULL,NULL,NULL,NULL,0,0,1,1,0,1,0,1,0,NULL,'T14'),
(38,'KR1234X',152999,1,'uploads/inventory/front_680e76203a0f6.jpg','2025-04-27 20:23:28','uploads/inventory/back_680e76203a41f.jpg','uploads/inventory/left_680e76203a6b4.jpg','uploads/inventory/right_680e76203a9d7.jpg',0,NULL,NULL,NULL,NULL,1,1,1,0,1,0,0,0,0,NULL,'T4'),
(39,'KR1234X',155006,1,'uploads/inventory/front_680fa99699d65.jpg','2025-04-28 18:15:18','uploads/inventory/back_680fa9969a0cd.jpg','uploads/inventory/left_680fa9969a37a.jpg','uploads/inventory/right_680fa9969a676.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'T14'),
(40,'WB12345',250000,0,'uploads/inventory/front_681882c28ae86.jpg','2025-05-05 11:20:02','uploads/inventory/back_681882c28b07b.jpg','uploads/inventory/left_681882c28b1e3.jpg','uploads/inventory/right_681882c28b34e.jpg',2,'uploads/inventory/dirt1_681882c28b4b2.jpg',NULL,NULL,NULL,1,0,1,0,1,1,0,0,1,NULL,'T14'),
(41,'DW1234X',1000000,1,'uploads/inventory/front_681b1a11ac948.jpg','2025-05-07 10:30:09','uploads/inventory/back_681b1a11acc68.jpg','uploads/inventory/left_681b1a11acec8.jpg','uploads/inventory/right_681b1a11ad0dd.jpg',5,NULL,NULL,NULL,NULL,1,1,1,1,1,1,1,1,1,NULL,'T1'),
(42,'ASDASD',250000,0,'uploads/inventory/front_681dcc426b981.jpg','2025-05-09 11:34:58','uploads/inventory/back_681dcc426bc36.jpg','uploads/inventory/left_681dcc426bdbb.jpg','uploads/inventory/right_681dcc426bee2.jpg',1,'uploads/inventory/dirt1_681dcc426c031.jpg',NULL,NULL,NULL,1,1,1,1,1,0,0,1,1,'sghj','T14'),
(43,'AWEAW',666666,1,'uploads/inventory/front_6821da4bad40e.jpg','2025-05-12 13:23:55','uploads/inventory/back_6821da4bad5ca.jpg','uploads/inventory/left_6821da4bad6de.jpg','uploads/inventory/right_6821da4bad7e5.jpg',5,NULL,NULL,NULL,NULL,1,0,1,0,0,1,0,0,1,NULL,'T14'),
(44,'SDFSDF',91000,1,'uploads/inventory/front_6823011316a8e.jpg','2025-05-13 10:21:39','uploads/inventory/back_6823011316cd4.jpg','uploads/inventory/left_6823011316e81.jpg','uploads/inventory/right_6823011317018.jpg',4,NULL,NULL,NULL,NULL,1,0,1,1,0,0,1,0,1,NULL,'T14'),
(45,'DW1234X',1000001,1,'uploads/inventory/front_68381b4298a58.jpg','2025-05-29 10:30:58','uploads/inventory/back_68381b4298ce8.jpg','uploads/inventory/left_68381b4298fa1.jpg','uploads/inventory/right_68381b429927a.jpg',4,NULL,NULL,NULL,NULL,0,0,1,0,1,0,1,1,1,NULL,'T14');
/*!40000 ALTER TABLE `inwentaryzacje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kierowcy`
--

DROP TABLE IF EXISTS `kierowcy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kierowcy` (
  `id` varchar(50) NOT NULL,
  `imie` varchar(100) NOT NULL,
  `nazwisko` varchar(100) NOT NULL,
  `saldo` decimal(10,2) DEFAULT 0.00,
  `password` varchar(255) NOT NULL,
  `rola` varchar(20) DEFAULT 'kierowca',
  `koszt_paliwa` varchar(20) DEFAULT 'firma',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `status` varchar(20) NOT NULL DEFAULT 'aktywny',
  `last_vehicle_plate` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kierowcy`
--

LOCK TABLES `kierowcy` WRITE;
/*!40000 ALTER TABLE `kierowcy` DISABLE KEYS */;
INSERT INTO `kierowcy` VALUES
('1','Jan','Kowalski',537.00,'abc123','kierowca','firma','2025-03-29 14:15:21','aktywny',NULL),
('10','Test','Kierowca',350.00,'4a44dc15364204a80fe80e9039455cc1608281820fe2b24f1e5233ade6af1dd5','kierowca','firma','2025-03-29 14:15:21','aktywny',NULL),
('2','Paweł','Turek',0.00,'d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35','kierowca','firma','2025-04-26 13:23:34','aktywny',NULL),
('3','Darek','Darek',0.00,'$2y$10$dew5WmScvalEtk3x16Ge2.2n/c8z7NGbbNyyKGVOQM5f5kWnGJKe2','kierowca','firma','2025-04-26 13:41:54','aktywny',NULL),
('T1','Arkadiusz','Ferenc',8978.18,'$2y$10$uGIBPfsEvgZAGXPY3o5dY.dVlxifsMAeCsEIvCSii96SvQVsX.W9S','kierowca','firma','2025-05-07 08:27:15','aktywny','DW1234X'),
('T14','Krzysztof','Golonka',8.52,'$2y$10$IOHu.J3yO/nH2kSyOGPaSOuZi5seLefG5OlUsaNZxqpKqdHanDIZG','kierowca','firma','2025-04-26 15:44:47','aktywny','DW1234X'),
('T66','hahah','',300.00,'69e91642bc538c7ae9a7686b0a56570ecd279e4976678267365dcedf5183f0b8','kierowca','firma','2025-06-17 12:47:24','aktywny',NULL);
/*!40000 ALTER TABLE `kierowcy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kursy`
--

DROP TABLE IF EXISTS `kursy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kursy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `saldo_wplyw` decimal(10,2) DEFAULT NULL,
  `saldo_po` decimal(10,2) DEFAULT NULL,
  `type` enum('Gotówka','Karta','Voucher') NOT NULL,
  `source` varchar(50) NOT NULL,
  `date` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `kursy_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `kierowcy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=235 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kursy`
--

LOCK TABLES `kursy` WRITE;
/*!40000 ALTER TABLE `kursy` DISABLE KEYS */;
INSERT INTO `kursy` VALUES
(94,'1',250.00,NULL,NULL,'Karta','Postój','2025-04-06 11:01:09'),
(95,'1',400.00,NULL,NULL,'Gotówka','Postój','2025-04-06 11:06:16'),
(96,'1',100.00,NULL,NULL,'Karta','Postój','2025-04-06 12:27:17'),
(97,'1',100.00,NULL,NULL,'Karta','Postój','2025-04-06 12:27:40'),
(98,'1',100.00,NULL,NULL,'Karta','Postój','2025-04-06 12:30:23'),
(99,'1',120.00,NULL,NULL,'Karta','Postój','2025-04-06 12:31:50'),
(100,'1',50.00,NULL,NULL,'Karta','Postój','2025-04-06 12:38:57'),
(101,'1',50.00,NULL,NULL,'Karta','Postój','2025-04-06 13:31:15'),
(102,'1',100.00,NULL,NULL,'Karta','Postój','2025-04-06 13:31:48'),
(103,'1',100.00,NULL,NULL,'Karta','Postój','2025-04-06 13:31:55'),
(208,'T14',100.00,38.80,38.80,'Karta','Postój','2025-04-27 19:57:53'),
(209,'T14',20.00,7.76,46.56,'Karta','Postój','2025-04-27 20:10:31'),
(213,'T14',150.00,58.20,154.76,'Karta','Postój','2025-04-29 10:10:45'),
(214,'T14',25.00,8.00,162.76,'Voucher','Dyspozytornia','2025-05-05 09:55:19'),
(215,'T14',150.00,-90.00,72.76,'Gotówka','Postój','2025-05-05 09:56:15'),
(216,'T14',50.00,15.40,88.16,'Karta','Dyspozytornia','2025-05-05 11:22:14'),
(217,'T14',150.00,-90.00,-1.84,'Gotówka','Postój','2025-05-05 11:22:34'),
(218,'T14',96.00,30.72,28.88,'Voucher','Dyspozytornia','2025-05-05 11:23:18'),
(219,'T1',50.00,0.00,10033.18,'Karta','Postój','2025-05-07 10:31:13'),
(220,'T1',2000.00,-160.00,9873.18,'Karta','Dyspozytornia','2025-05-07 10:31:54'),
(221,'T1',250.00,180.00,10053.18,'Voucher','Dyspozytornia','2025-05-07 10:32:17'),
(222,'T14',213.00,68.16,97.04,'Voucher','Dyspozytornia','2025-05-09 11:35:21'),
(223,'T14',200.00,-120.00,-72.96,'Gotówka','Postój','2025-05-12 13:24:20'),
(224,'T14',93.00,29.76,-43.20,'Voucher','Dyspozytornia','2025-05-12 13:24:46'),
(225,'T14',500.00,194.00,150.80,'Karta','Postój','2025-05-12 13:25:50'),
(226,'T14',100.00,38.80,38.80,'Karta','Postój','2025-05-13 10:22:14'),
(227,'T14',150.00,-102.00,-63.20,'Gotówka','Dyspozytornia','2025-05-13 10:22:34'),
(228,'T14',150.00,-90.00,-493.20,'Gotówka','Postój','2025-05-13 10:53:20'),
(229,'T14',100.00,38.80,-454.40,'Karta','Postój','2025-05-13 10:53:34'),
(230,'T14',150.00,58.20,-396.20,'Karta','Postój','2025-05-13 12:08:13'),
(231,'T14',200.00,61.60,-334.60,'Karta','Dyspozytornia','2025-05-13 12:08:40'),
(232,'T14',111.00,35.52,-299.08,'Voucher','Dyspozytornia','2025-05-13 12:08:59'),
(233,'T14',250.00,-170.00,-69.08,'Gotówka','Dyspozytornia','2025-05-16 15:41:58'),
(234,'T14',200.00,77.60,8.52,'Karta','Postój','2025-05-29 10:31:34');
/*!40000 ALTER TABLE `kursy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manual_balance_updates`
--

DROP TABLE IF EXISTS `manual_balance_updates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manual_balance_updates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) DEFAULT NULL,
  `amount` float NOT NULL,
  `reason` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `manual_balance_updates_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manual_balance_updates`
--

LOCK TABLES `manual_balance_updates` WRITE;
/*!40000 ALTER TABLE `manual_balance_updates` DISABLE KEYS */;
/*!40000 ALTER TABLE `manual_balance_updates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pojazdy`
--

DROP TABLE IF EXISTS `pojazdy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pojazdy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rejestracja` varchar(20) NOT NULL,
  `marka` varchar(50) DEFAULT NULL,
  `model` varchar(50) DEFAULT NULL,
  `przebieg` int(11) DEFAULT 0 CHECK (`przebieg` >= 0),
  `ubezpieczenie_do` date DEFAULT NULL,
  `przeglad_do` date DEFAULT NULL,
  `aktywny` tinyint(1) DEFAULT 1,
  `ostatni_kierowca_id` varchar(50) DEFAULT NULL,
  `inpost` tinyint(1) DEFAULT 0,
  `taxi` tinyint(1) DEFAULT 0,
  `taksometr` tinyint(1) DEFAULT 0,
  `legalizacja_taksometru_do` date DEFAULT NULL,
  `gaz` tinyint(1) DEFAULT 0,
  `homologacja_lpg_do` date DEFAULT NULL,
  `firma` varchar(50) DEFAULT NULL,
  `forma_wlasnosci` varchar(50) DEFAULT NULL,
  `numer_polisy` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rejestracja` (`rejestracja`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pojazdy`
--

LOCK TABLES `pojazdy` WRITE;
/*!40000 ALTER TABLE `pojazdy` DISABLE KEYS */;
INSERT INTO `pojazdy` VALUES
(4,'wqeqwe','qweq','wqeqw',12345,'2025-04-24','2025-05-06',1,NULL,0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(5,'aweaw','asdasd','asdasd',777777,'2025-05-03','2026-04-16',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(6,'sdfsdf','asdfsad','sadfasdf',91000,'2025-04-23','2025-05-10',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(7,'KR1234X','Toyota','Corolla',155009,'2025-12-31','2025-12-31',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(8,'DW1234X','Toyota','Corolla',1000001,'2025-07-23','2025-08-29',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(9,'DKL82535','Mercedes-Benz','V-klasse',1,'2026-04-15','2026-04-18',1,NULL,0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(10,'DW9YF48','Toyota','Corolla',180000,'2026-06-27','2026-07-02',1,NULL,0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(11,'DX23YB','Toyo','Coro',172888,'2025-06-14','2025-06-20',1,NULL,0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(12,'DW9999','FIAT','125P',300000,'2026-06-24','2026-04-14',1,NULL,0,0,0,NULL,0,NULL,'OCTO','własna','909090'),
(13,'DW0000','Fiat','OOOO',190000,'2025-06-12','2025-06-15',0,NULL,1,1,1,'2025-06-04',1,'2025-06-21','FUN','własny','polisa0000');
/*!40000 ALTER TABLE `pojazdy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pracownicy`
--

DROP TABLE IF EXISTS `pracownicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pracownicy` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `firma` varchar(100) DEFAULT NULL,
  `rodzaj_umowy` varchar(100) DEFAULT NULL,
  `data_umowy` date DEFAULT NULL,
  `dowod` tinyint(1) NOT NULL DEFAULT 0,
  `prawo_jazdy` tinyint(1) NOT NULL DEFAULT 0,
  `niekaralnosc` tinyint(1) NOT NULL DEFAULT 0,
  `orzeczenie_psychologiczne` tinyint(1) NOT NULL DEFAULT 0,
  `data_badania_psychologicznego` date DEFAULT NULL,
  `orzeczenie_lekarskie` tinyint(1) NOT NULL DEFAULT 0,
  `data_badan_lekarskich` date DEFAULT NULL,
  `informacja_ppk` tinyint(1) NOT NULL DEFAULT 0,
  `rezygnacja_ppk` tinyint(1) NOT NULL DEFAULT 0,
  `forma_wyplaty` varchar(50) DEFAULT NULL,
  `wynagrodzenie_do_rak_wlasnych` tinyint(1) NOT NULL DEFAULT 0,
  `zgoda_na_przelew` tinyint(1) NOT NULL DEFAULT 0,
  `ryzyko_zawodowe` tinyint(1) NOT NULL DEFAULT 0,
  `oswiadczenie_zus` tinyint(1) NOT NULL DEFAULT 0,
  `bhp` tinyint(1) NOT NULL DEFAULT 0,
  `regulamin_pracy` tinyint(1) NOT NULL DEFAULT 0,
  `zasady_ewidencji_kasa` tinyint(1) NOT NULL DEFAULT 0,
  `pit2` tinyint(1) NOT NULL DEFAULT 0,
  `oswiadczenie_art188_kp` tinyint(1) NOT NULL DEFAULT 0,
  `rodo` tinyint(1) NOT NULL DEFAULT 0,
  `pora_nocna` tinyint(1) NOT NULL DEFAULT 0,
  `pit_email` varchar(100) DEFAULT NULL,
  `osoba_kontaktowa` varchar(100) DEFAULT NULL,
  `numer_prywatny` varchar(50) DEFAULT NULL,
  `numer_sluzbowy` varchar(50) DEFAULT NULL,
  `model_telefonu_sluzbowego` varchar(100) DEFAULT NULL,
  `operator` varchar(100) DEFAULT NULL,
  `waznosc_wizy` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pracownicy`
--

LOCK TABLES `pracownicy` WRITE;
/*!40000 ALTER TABLE `pracownicy` DISABLE KEYS */;
/*!40000 ALTER TABLE `pracownicy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refuels`
--

DROP TABLE IF EXISTS `refuels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refuels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) DEFAULT NULL,
  `refuel_date` datetime NOT NULL,
  `fuel_amount` float NOT NULL,
  `cost` float NOT NULL,
  `odometer` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `refuels_ibfk_1` (`driver_id`),
  CONSTRAINT `refuels_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `kierowcy` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refuels`
--

LOCK TABLES `refuels` WRITE;
/*!40000 ALTER TABLE `refuels` DISABLE KEYS */;
INSERT INTO `refuels` VALUES
(3,'T14','2025-04-29 16:02:45',30,150,155010,'2025-04-29 14:02:45');
/*!40000 ALTER TABLE `refuels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trips`
--

DROP TABLE IF EXISTS `trips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) DEFAULT NULL,
  `trip_date` datetime NOT NULL,
  `source` varchar(255) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `payment_type` varchar(50) DEFAULT NULL,
  `amount` float NOT NULL,
  `distance` float NOT NULL,
  `commission` float NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `trips_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trips`
--

LOCK TABLES `trips` WRITE;
/*!40000 ALTER TABLE `trips` DISABLE KEYS */;
/*!40000 ALTER TABLE `trips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('kierowca','flotowiec','admin') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,'admin','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','admin'),
(2,'flota1','8b046d263fdc049847e891edc83077d8684ea98cba7b7ae72deb64eab14939f1','flotowiec'),
(3,'kierowca1','a89abdeecdb653e7b027d5314df4a02cfbdbf18bf53fd7ef807486af14e1e963','kierowca');
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

-- Dump completed on 2025-07-08 14:01:52
