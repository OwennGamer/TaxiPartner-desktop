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
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaboration_terms`
--

LOCK TABLES `collaboration_terms` WRITE;
/*!40000 ALTER TABLE `collaboration_terms` DISABLE KEYS */;
INSERT INTO `collaboration_terms` VALUES
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
(110,'T66','percentTurnover','20.1'),
(111,'T66','fuelCost','0'),
(112,'T66','cardCommission','90.1'),
(113,'T66','partnerCommission','10'),
(114,'T66','boltCommission','10.98'),
(115,'T66','settlementLimit','500.8'),
(116,'T99','percentTurnover','40'),
(117,'T99','fuelCost','0'),
(118,'T99','cardCommission','3'),
(119,'T99','partnerCommission','20'),
(120,'T99','boltCommission','20'),
(121,'T99','settlementLimit','200'),
(122,'T98','percentTurnover','40'),
(123,'T98','fuelCost','0'),
(124,'T98','cardCommission','3'),
(125,'T98','partnerCommission','20'),
(126,'T98','boltCommission','20'),
(127,'T98','settlementLimit','500'),
(128,'T108','percentTurnover','40'),
(129,'T108','fuelCost','0'),
(130,'T108','cardCommission','3'),
(131,'T108','partnerCommission','0'),
(132,'T108','boltCommission','0'),
(133,'T108','settlementLimit','5000'),
(134,'T0999','percentTurnover','40'),
(135,'T0999','fuelCost','0'),
(136,'T0999','cardCommission','3'),
(137,'T0999','partnerCommission','20'),
(138,'T0999','boltCommission','20'),
(139,'T0999','settlementLimit','100'),
(140,'T088','percentTurnover','40'),
(141,'T088','fuelCost','0'),
(142,'T088','cardCommission','3'),
(143,'T088','partnerCommission','20'),
(144,'T088','boltCommission','20'),
(145,'T088','settlementLimit','1000'),
(146,'T50','percentTurnover','40'),
(147,'T50','fuelCost','0'),
(148,'T50','cardCommission','3'),
(149,'T50','partnerCommission','20'),
(150,'T50','boltCommission','20'),
(151,'T50','settlementLimit','1000');
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
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
(34,'T66',-11.87,300.00,'Rozliczenie',NULL,'2025-06-17 15:07:54'),
(35,'T99',-50.00,112.32,'brak zdjęcia paragonu',NULL,'2025-07-30 13:36:13'),
(36,'T108',-1033.00,2482.20,'Rozliczenie',NULL,'2025-08-01 11:42:01'),
(37,'T108',0.00,2482.20,'Rozliczenie',NULL,'2025-08-01 11:43:38'),
(38,'T108',-0.51,2481.69,'Rozliczenie',NULL,'2025-08-01 11:44:05'),
(39,'T108',0.60,2481.69,'Rozliczenie',NULL,'2025-08-01 11:52:28'),
(40,'T99',100.30,251.42,'Kara',NULL,'2025-08-04 12:41:15'),
(41,'T99',100.20,351.62,'Rozliczenie',NULL,'2025-08-04 13:18:02'),
(42,'T99',-50.00,147.58,'Kara',NULL,'2025-08-07 09:29:05'),
(43,'T99',-50.00,97.58,'Kara',NULL,'2025-08-07 15:53:59'),
(44,'T99',200.00,297.58,'Premia',NULL,'2025-08-07 15:55:39'),
(45,'T99',200.00,497.58,'Rozliczenie',NULL,'2025-08-07 15:56:03'),
(46,'T99',-40.00,457.58,'Kara',NULL,'2025-08-07 16:15:40'),
(47,'T99',25.00,482.58,'Premia',NULL,'2025-08-07 16:25:59'),
(48,'T99',-45.00,437.58,'Kara',NULL,'2025-08-11 17:09:16'),
(49,'T99',60.00,497.58,'Premia',NULL,'2025-08-12 19:30:12'),
(50,'T99',-60.00,437.58,'Rozliczenie',NULL,'2025-08-12 19:31:01'),
(51,'T99',21.00,458.58,'Premia',NULL,'2025-08-12 19:46:25'),
(52,'T99',1.00,459.58,'diagnostic',NULL,'2025-08-14 17:39:23'),
(53,'T99',1.00,460.58,'diagnostic',NULL,'2025-08-14 17:59:18'),
(54,'T99',1.00,461.58,'diagnostic',NULL,'2025-08-18 10:25:00'),
(55,'T99',100.00,561.58,'Rozliczenie',NULL,'2025-08-18 14:48:05'),
(56,'T99',-500.00,61.58,'Kara',NULL,'2025-08-18 14:49:37'),
(57,'T99',-30.00,31.58,'Jesteś zjebany',NULL,'2025-08-18 14:51:10');
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
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
(45,'DW1234X',1000001,1,'uploads/inventory/front_68381b4298a58.jpg','2025-05-29 10:30:58','uploads/inventory/back_68381b4298ce8.jpg','uploads/inventory/left_68381b4298fa1.jpg','uploads/inventory/right_68381b429927a.jpg',4,NULL,NULL,NULL,NULL,0,0,1,0,1,0,1,1,1,NULL,'T14'),
(46,'DX23YB',173000,1,'uploads/inventory/front_68876b418e9ea.jpg','2025-07-28 14:21:21','uploads/inventory/back_68876b418ec7e.jpg','uploads/inventory/left_68876b418eeb8.jpg','uploads/inventory/right_68876b418f0f4.jpg',0,NULL,NULL,NULL,NULL,1,1,1,1,0,0,0,1,0,NULL,'T99'),
(47,'DX23YB',175000,1,'uploads/inventory/front_6887ca0594edf.jpg','2025-07-28 21:05:41','uploads/inventory/back_6887ca0595171.jpg','uploads/inventory/left_6887ca05953d3.jpg','uploads/inventory/right_6887ca05955dc.jpg',0,NULL,NULL,NULL,NULL,1,1,0,1,0,0,0,0,0,NULL,'T98'),
(48,'DX23YB',178001,1,'uploads/inventory/front_6889fe0c11ea7.jpg','2025-07-30 13:12:12','uploads/inventory/back_6889fe0c12129.jpg','uploads/inventory/left_6889fe0c12331.jpg','uploads/inventory/right_6889fe0c124ff.jpg',0,NULL,NULL,NULL,NULL,0,1,0,1,1,0,0,0,0,NULL,'T99'),
(49,'DKL83517',2,1,'uploads/inventory/front_688c88ac6970d.jpg','2025-08-01 11:28:12','uploads/inventory/back_688c88ac698d8.jpg','uploads/inventory/left_688c88ac69a23.jpg','uploads/inventory/right_688c88ac69b84.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,'Nie ','T108'),
(50,'DX23YB',186001,1,'uploads/inventory/front_688c8b8a6138a.jpg','2025-08-01 11:40:26','uploads/inventory/back_688c8b8a61795.jpg','uploads/inventory/left_688c8b8a61b4d.jpg','uploads/inventory/right_688c8b8a61e23.jpg',5,NULL,NULL,NULL,NULL,1,1,1,0,1,1,1,1,1,NULL,'T108'),
(51,'DW9YF48',180001,1,'uploads/inventory/front_6891fb60023fe.jpg','2025-08-05 14:38:56','uploads/inventory/back_6891fb60026c8.jpg','uploads/inventory/left_6891fb60028e5.jpg','uploads/inventory/right_6891fb6002b0b.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'T99'),
(52,'DX23YB',187000,1,'uploads/inventory/front_68a32254cb006.jpg','2025-08-18 14:53:40','uploads/inventory/back_68a32254cb345.jpg','uploads/inventory/left_68a32254cb5d2.jpg','uploads/inventory/right_68a32254cb7ff.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'T99'),
(53,'DW9YF48',201000,1,'uploads/inventory/front_68a45389262e2.jpg','2025-08-19 12:35:53','uploads/inventory/back_68a45389266c0.jpg','uploads/inventory/left_68a4538926a91.jpg','uploads/inventory/right_68a4538926d81.jpg',0,NULL,NULL,NULL,NULL,0,0,1,0,1,0,0,1,0,NULL,'T50');
/*!40000 ALTER TABLE `inwentaryzacje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jwt_tokens`
--

DROP TABLE IF EXISTS `jwt_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jwt_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` text NOT NULL,
  `driver_id` varchar(50) NOT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jwt_tokens`
--

LOCK TABLES `jwt_tokens` WRITE;
/*!40000 ALTER TABLE `jwt_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `jwt_tokens` ENABLE KEYS */;
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
  `fcm_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kierowcy`
--

LOCK TABLES `kierowcy` WRITE;
/*!40000 ALTER TABLE `kierowcy` DISABLE KEYS */;
INSERT INTO `kierowcy` VALUES
('3','Darek','Darek',0.00,'$2y$10$dew5WmScvalEtk3x16Ge2.2n/c8z7NGbbNyyKGVOQM5f5kWnGJKe2','kierowca','firma','2025-04-26 13:41:54','aktywny',NULL,NULL),
('T088','QQQQ','',0.00,'489cd5dbc708c7e541de4d7cd91ce6d0f1613573b7fc5b40d3942ccb9555cf35','kierowca','firma','2025-08-05 11:40:29','aktywny',NULL,NULL),
('T0999','Jaka','',100.00,'489cd5dbc708c7e541de4d7cd91ce6d0f1613573b7fc5b40d3942ccb9555cf35','kierowca','firma','2025-08-05 11:39:36','aktywny',NULL,NULL),
('T1','Arkadiusz','Ferenc',8978.18,'$2y$10$uGIBPfsEvgZAGXPY3o5dY.dVlxifsMAeCsEIvCSii96SvQVsX.W9S','kierowca','firma','2025-05-07 08:27:15','aktywny','DW1234X',NULL),
('T108','Michał','Miszczyk',5646.37,'$2y$10$d4z9GpjgCNRRtysqtLqxAOE2BTXAWoDR.Hi7Mtsf/3RS8WT.QgSSu','kierowca','firma','2025-08-01 09:18:57','aktywny','DX23YB',NULL),
('T14','Krzysztof','Golonka',-3.88,'$2y$10$dwVJmBxtGzlca0VgHuCjNOw0SP15P29gr3m4jnLmU2KRa1T86fvkq','kierowca','firma','2025-04-26 15:44:47','aktywny','DW1234X',NULL),
('T50','Flotowiec','',38.80,'$2y$10$9ezp.PasPVrDdNe9JgOtfeJcn0KzEj.Z.38QPfTksVRoZSiyjM3D2','flotowiec','firma','2025-08-19 10:31:29','aktywny','DW9YF48',NULL),
('T66','hahah','',300.00,'69e91642bc538c7ae9a7686b0a56570ecd279e4976678267365dcedf5183f0b8','kierowca','firma','2025-06-17 12:47:24','aktywny',NULL,NULL),
('T98','Teścik','Teściowski',113.92,'$2y$10$N7SV5.gOc1Tv4kK3lrc8Q.lGt8ewizI0bkYUtDtgx1y/QwjDMrBzm','kierowca','firma','2025-07-28 19:04:18','aktywny','DX23YB',NULL),
('T99','Nowy','Kierowca',70.38,'$2y$10$rWoFH4IWmmolt6u4LSZ.Qe1zKC0ZaQE1qNrO/hjvP.LdJmHcaiGye','kierowca','firma','2025-07-28 12:18:16','aktywny','DX23YB','eqfbFSvXQn2kMbw2QgCTyM:APA91bGNdc3EtnfF9sdAoGct-zHDSPWyFXXke-JPqyASsT8gdJQO0VfZlqHH_Wt1g9BThWiH4w9nHZxrIsl9w7CU-rLhtal9EvN9b8o3TWhxUfqHTbNStGQ');
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
  `via_km` tinyint(1) NOT NULL DEFAULT 0,
  `date` datetime DEFAULT current_timestamp(),
  `receipt_photo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `kursy_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `kierowcy` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=324 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kursy`
--

LOCK TABLES `kursy` WRITE;
/*!40000 ALTER TABLE `kursy` DISABLE KEYS */;
INSERT INTO `kursy` VALUES
(208,'T14',100.00,38.80,38.80,'Karta','Postój',0,'2025-04-27 19:57:53',NULL),
(209,'T14',20.00,7.76,46.56,'Karta','Postój',0,'2025-04-27 20:10:31',NULL),
(213,'T14',150.00,58.20,154.76,'Karta','Postój',0,'2025-04-29 10:10:45',NULL),
(214,'T14',25.00,8.00,162.76,'Voucher','Dyspozytornia',0,'2025-05-05 09:55:19',NULL),
(215,'T14',150.00,-90.00,72.76,'Gotówka','Postój',0,'2025-05-05 09:56:15',NULL),
(216,'T14',50.00,15.40,88.16,'Karta','Dyspozytornia',0,'2025-05-05 11:22:14',NULL),
(217,'T14',150.00,-90.00,-1.84,'Gotówka','Postój',0,'2025-05-05 11:22:34',NULL),
(218,'T14',96.00,30.72,28.88,'Voucher','Dyspozytornia',0,'2025-05-05 11:23:18',NULL),
(219,'T1',50.00,0.00,10033.18,'Karta','Postój',0,'2025-05-07 10:31:13',NULL),
(220,'T1',2000.00,-160.00,9873.18,'Karta','Dyspozytornia',0,'2025-05-07 10:31:54',NULL),
(221,'T1',250.00,180.00,10053.18,'Voucher','Dyspozytornia',0,'2025-05-07 10:32:17',NULL),
(222,'T14',213.00,68.16,97.04,'Voucher','Dyspozytornia',0,'2025-05-09 11:35:21',NULL),
(223,'T14',200.00,-120.00,-72.96,'Gotówka','Postój',0,'2025-05-12 13:24:20',NULL),
(224,'T14',93.00,29.76,-43.20,'Voucher','Dyspozytornia',0,'2025-05-12 13:24:46',NULL),
(225,'T14',500.00,194.00,150.80,'Karta','Postój',0,'2025-05-12 13:25:50',NULL),
(226,'T14',100.00,38.80,38.80,'Karta','Postój',0,'2025-05-13 10:22:14',NULL),
(227,'T14',150.00,-102.00,-63.20,'Gotówka','Dyspozytornia',0,'2025-05-13 10:22:34',NULL),
(228,'T14',150.00,-90.00,-493.20,'Gotówka','Postój',0,'2025-05-13 10:53:20',NULL),
(229,'T14',100.00,38.80,-454.40,'Karta','Postój',0,'2025-05-13 10:53:34',NULL),
(230,'T14',150.00,58.20,-396.20,'Karta','Postój',0,'2025-05-13 12:08:13',NULL),
(231,'T14',200.00,61.60,-334.60,'Karta','Dyspozytornia',0,'2025-05-13 12:08:40',NULL),
(232,'T14',111.00,35.52,-299.08,'Voucher','Dyspozytornia',0,'2025-05-13 12:08:59',NULL),
(233,'T14',250.00,-170.00,-69.08,'Gotówka','Dyspozytornia',0,'2025-05-16 15:41:58',NULL),
(234,'T14',200.00,77.60,8.52,'Karta','Postój',0,'2025-05-29 10:31:34',NULL),
(235,'T99',100.00,38.80,38.80,'Karta','Postój',0,'2025-07-28 14:21:40',NULL),
(236,'T99',150.00,58.20,97.00,'Karta','Postój',0,'2025-07-28 17:17:03',NULL),
(237,'T99',50.00,19.40,116.40,'Karta','Postój',0,'2025-07-28 18:08:22',NULL),
(238,'T99',72.00,23.04,139.44,'Voucher','Dyspozytornia',0,'2025-07-28 18:34:01',NULL),
(239,'T99',100.00,-60.00,79.44,'Gotówka','Postój',0,'2025-07-28 18:54:18',NULL),
(240,'T14',100.00,38.80,47.32,'Karta','Postój',0,'2025-07-28 18:57:58',NULL),
(241,'T14',150.00,-90.00,-42.68,'Gotówka','Postój',0,'2025-07-28 18:58:54',NULL),
(242,'T14',100.00,38.80,-3.88,'Karta','Postój',0,'2025-07-28 19:03:18',NULL),
(243,'T99',100.00,32.00,111.44,'Voucher','Postój',0,'2025-07-28 19:22:17',NULL),
(244,'T99',48.00,15.36,126.80,'Voucher','Dyspozytornia',0,'2025-07-28 19:25:28',NULL),
(245,'T99',111.00,35.52,162.32,'Voucher','Dyspozytornia',1,'2025-07-28 21:00:47',NULL),
(246,'T98',10.00,-6.80,-6.80,'Gotówka','Dyspozytornia',0,'2025-07-28 21:05:57',NULL),
(247,'T98',48.00,15.36,8.56,'Voucher','Dyspozytornia',1,'2025-07-28 21:06:28',NULL),
(248,'T98',25.00,8.00,16.56,'Voucher','Dyspozytornia',0,'2025-07-28 21:08:26',NULL),
(249,'T98',50.00,16.00,32.56,'Voucher','Dyspozytornia',0,'2025-07-28 21:09:22',NULL),
(250,'T98',133.00,42.56,75.12,'Voucher','Dyspozytornia',1,'2025-07-28 21:19:17',NULL),
(251,'T98',100.00,38.80,113.92,'Karta','Postój',0,'2025-07-28 21:19:41',NULL),
(252,'T99',100.00,38.80,151.12,'Karta','Postój',0,'2025-07-31 13:27:13',NULL),
(253,'T108',48.00,19.20,3515.20,'Voucher','Dyspozytornia',1,'2025-08-01 11:29:47',NULL),
(254,'T108',1.00,-0.60,2481.09,'Gotówka','Postój',0,'2025-08-01 11:45:42',NULL),
(255,'T108',90.00,34.92,2516.61,'Karta','Postój',0,'2025-08-01 15:30:32',NULL),
(256,'T108',190.00,76.00,2592.61,'Voucher','Dyspozytornia',0,'2025-08-01 17:55:20',NULL),
(257,'T108',200.00,77.60,2670.21,'Karta','Dyspozytornia',0,'2025-08-02 13:45:08',NULL),
(258,'T108',190.00,76.00,2746.21,'Voucher','Dyspozytornia',0,'2025-08-02 17:47:01',NULL),
(259,'T108',150.00,58.20,2804.41,'Karta','Dyspozytornia',0,'2025-08-04 09:39:47',NULL),
(260,'T108',50.00,-30.00,2774.41,'Gotówka','Postój',0,'2025-08-04 14:13:37',NULL),
(261,'T108',150.00,58.20,2832.61,'Karta','Dyspozytornia',0,'2025-08-05 06:52:47',NULL),
(262,'T108',150.00,58.20,2890.81,'Karta','Dyspozytornia',0,'2025-08-05 08:00:39',NULL),
(263,'T108',97.00,-58.20,2832.61,'Gotówka','Postój',0,'2025-08-05 11:44:12',NULL),
(264,'T108',95.00,36.86,2869.47,'Karta','Postój',0,'2025-08-05 15:26:25',NULL),
(265,'T99',100.00,38.80,390.42,'Karta','Postój',0,'2025-08-05 16:56:43',NULL),
(266,'T99',100.00,-60.00,330.42,'Gotówka','Postój',0,'2025-08-05 16:57:23',NULL),
(267,'T99',100.00,38.80,369.22,'Karta','Postój',0,'2025-08-05 17:01:12',NULL),
(268,'T108',150.00,58.20,2927.67,'Karta','Dyspozytornia',0,'2025-08-05 18:00:14',NULL),
(269,'T108',150.00,58.20,2985.87,'Karta','Dyspozytornia',0,'2025-08-06 07:58:59',NULL),
(270,'T108',80.00,31.04,3016.91,'Karta','Postój',0,'2025-08-06 12:12:52',NULL),
(271,'T99',100.00,38.80,408.02,'Karta','Postój',0,'2025-08-06 14:00:09',NULL),
(272,'T99',550.00,-330.00,78.02,'Gotówka','Postój',0,'2025-08-06 14:01:27',NULL),
(273,'T99',300.00,92.40,170.42,'Karta','Dyspozytornia',0,'2025-08-06 14:02:19',NULL),
(274,'T99',50.00,19.40,189.82,'Karta','Postój',0,'2025-08-06 14:17:10',NULL),
(275,'T99',20.00,7.76,197.58,'Karta','Postój',0,'2025-08-06 14:41:13',NULL),
(276,'T108',100.00,38.80,3055.71,'Karta','Postój',0,'2025-08-06 15:19:53',NULL),
(277,'T108',150.00,58.20,3113.91,'Karta','Dyspozytornia',0,'2025-08-06 18:09:33',NULL),
(278,'T108',150.00,58.20,3172.11,'Karta','Dyspozytornia',0,'2025-08-07 07:54:04',NULL),
(279,'T108',75.00,30.00,3202.11,'Voucher','Dyspozytornia',0,'2025-08-07 10:00:22',NULL),
(280,'T108',35.00,13.58,3215.69,'Karta','Dyspozytornia',0,'2025-08-07 10:23:51',NULL),
(281,'T108',150.00,58.20,3273.89,'Karta','Dyspozytornia',0,'2025-08-07 13:43:07',NULL),
(282,'T99',100.00,38.80,70.38,'Karta','Postój',0,'2025-08-18 15:02:31','uploads/receipts/receipt_68a324673f3ec.jpg'),
(283,'T108',199.00,79.60,3353.49,'Voucher','Dyspozytornia',0,'2025-08-18 17:58:46',NULL),
(284,'T108',450.00,180.00,3533.49,'Voucher','Dyspozytornia',0,'2025-08-18 23:53:58',NULL),
(285,'T108',594.00,230.47,3763.96,'Karta','Dyspozytornia',0,'2025-08-19 15:10:42',NULL),
(286,'T108',105.00,42.00,3805.96,'Voucher','Dyspozytornia',0,'2025-08-20 05:37:17',NULL),
(287,'T108',180.00,69.84,3875.80,'Karta','Dyspozytornia',0,'2025-08-20 09:30:42',NULL),
(288,'T108',90.00,-54.00,3821.80,'Gotówka','Postój',0,'2025-08-20 11:02:07',NULL),
(289,'T108',80.00,31.04,3852.84,'Karta','Postój',0,'2025-08-20 13:55:14',NULL),
(290,'T108',105.00,42.00,3894.84,'Voucher','Dyspozytornia',0,'2025-08-20 17:45:05',NULL),
(291,'T108',105.00,42.00,3936.84,'Voucher','Dyspozytornia',0,'2025-08-21 05:32:05',NULL),
(292,'T108',120.00,46.56,3983.40,'Karta','Postój',0,'2025-08-21 13:57:06',NULL),
(293,'T108',105.00,42.00,4025.40,'Voucher','Dyspozytornia',0,'2025-08-21 17:34:29',NULL),
(294,'T108',105.00,42.00,4067.40,'Voucher','Dyspozytornia',0,'2025-08-22 05:27:39',NULL),
(295,'T108',75.00,30.00,4097.40,'Voucher','Dyspozytornia',0,'2025-08-22 09:38:32',NULL),
(296,'T108',115.00,44.62,4142.02,'Karta','Postój',0,'2025-08-22 12:58:41',NULL),
(297,'T108',450.00,180.00,4322.02,'Voucher','Dyspozytornia',0,'2025-08-22 16:23:06',NULL),
(298,'T108',500.00,200.00,4522.02,'Voucher','Dyspozytornia',0,'2025-08-23 04:24:23',NULL),
(299,'T108',280.00,108.64,4630.66,'Karta','Dyspozytornia',0,'2025-08-25 11:42:07',NULL),
(300,'T108',100.00,38.80,4669.46,'Karta','Dyspozytornia',0,'2025-08-25 11:42:16',NULL),
(301,'T108',250.00,97.00,4766.46,'Karta','Dyspozytornia',0,'2025-08-25 14:02:55',NULL),
(302,'T108',200.00,77.60,4844.06,'Karta','Dyspozytornia',0,'2025-08-25 18:14:38',NULL),
(303,'T108',210.00,84.00,4928.06,'Voucher','Dyspozytornia',0,'2025-08-26 06:01:58',NULL),
(304,'T108',200.00,77.60,5005.66,'Karta','Dyspozytornia',0,'2025-08-26 08:37:54',NULL),
(305,'T108',90.00,-54.00,4951.66,'Gotówka','Postój',0,'2025-08-26 11:01:14',NULL),
(306,'T108',120.00,46.56,4998.22,'Karta','Postój',0,'2025-08-26 13:52:47',NULL),
(307,'T108',200.00,77.60,5075.82,'Karta','Dyspozytornia',0,'2025-08-26 16:00:20',NULL),
(308,'T108',180.00,72.00,5147.82,'Voucher','Dyspozytornia',0,'2025-08-26 17:56:11',NULL),
(309,'T108',200.00,77.60,5225.42,'Karta','Dyspozytornia',0,'2025-08-27 08:05:33',NULL),
(310,'T108',141.00,54.71,5280.13,'Karta','Postój',0,'2025-08-27 12:25:57',NULL),
(311,'T108',167.00,-100.20,5179.93,'Gotówka','Postój',0,'2025-08-27 14:58:47',NULL),
(312,'T108',400.00,155.20,5335.13,'Karta','Dyspozytornia',0,'2025-08-27 22:17:01',NULL),
(313,'T108',200.00,77.60,5412.73,'Karta','Dyspozytornia',0,'2025-08-28 08:06:41',NULL),
(314,'T108',150.00,58.20,5470.93,'Karta','Dyspozytornia',0,'2025-08-28 12:35:55',NULL),
(315,'T108',100.00,-60.00,5410.93,'Gotówka','Postój',0,'2025-08-28 12:36:00',NULL),
(316,'T108',100.00,-60.00,5350.93,'Gotówka','Postój',0,'2025-08-28 15:41:56',NULL),
(317,'T108',105.00,42.00,5392.93,'Voucher','Dyspozytornia',0,'2025-08-28 17:41:48',NULL),
(318,'T108',120.00,46.56,5439.49,'Karta','Postój',0,'2025-08-29 11:27:59',NULL),
(319,'T108',200.00,77.60,5517.09,'Karta','Dyspozytornia',0,'2025-08-29 11:29:08',NULL),
(320,'T108',127.00,49.28,5566.37,'Karta','Postój',0,'2025-08-29 15:38:55',NULL),
(321,'T108',105.00,42.00,5608.37,'Voucher','Dyspozytornia',0,'2025-09-01 05:33:45',NULL),
(322,'T108',95.00,38.00,5646.37,'Voucher','Dyspozytornia',0,'2025-09-01 09:25:39',NULL),
(323,'T50',100.00,38.80,38.80,'Karta','Postój',0,'2025-09-01 14:09:56','uploads/receipts/receipt_68b58d14848c4.jpg');
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pojazdy`
--

LOCK TABLES `pojazdy` WRITE;
/*!40000 ALTER TABLE `pojazdy` DISABLE KEYS */;
INSERT INTO `pojazdy` VALUES
(6,'sdfsdf','asdfsad','sadfasdf',91000,'2025-04-23','2025-05-10',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(7,'KR1234X','Toyota','Corolla',155009,'2025-12-31','2025-12-31',1,'T14',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(8,'DW1234X','Toyota','Corolla',1000002,'2025-07-23','2025-08-29',1,'T14',1,1,1,'2025-08-29',1,'2025-08-31','FUN','własne','1231231'),
(9,'DKL82535','Mercedes-Benz','V-klasse',2,'2026-04-15','2026-04-18',1,NULL,0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(10,'DW9YF48','Toyota','Corolla',222035,'2026-06-27','2026-07-02',1,'T50',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(11,'DX23YB','Toyo','Coro',195000,'2025-06-14','2025-06-20',1,'T99',0,0,0,NULL,0,NULL,NULL,NULL,NULL),
(13,'DW0000','Fiat','OOOO',190000,'2025-06-12','2025-06-15',0,NULL,1,1,1,'2025-06-04',1,'2025-06-21','FUN','własny','polisa0000'),
(17,'DKL83517','mercedes-benz','vito',94380,'2025-09-15','2025-09-15',1,'T108',0,1,1,'2025-09-15',0,NULL,'POLCAR','leasing','123');
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
  `name` varchar(100) DEFAULT NULL,
  `firma` varchar(100) DEFAULT NULL,
  `rodzaj_umowy` varchar(100) DEFAULT NULL,
  `data_umowy` date DEFAULT NULL,
  `dowod` tinyint(1) DEFAULT NULL,
  `prawo_jazdy` tinyint(1) DEFAULT NULL,
  `niekaralnosc` tinyint(1) DEFAULT NULL,
  `orzeczenie_psychologiczne` tinyint(1) DEFAULT NULL,
  `data_badania_psychologicznego` date DEFAULT NULL,
  `orzeczenie_lekarskie` tinyint(1) DEFAULT NULL,
  `data_badan_lekarskich` date DEFAULT NULL,
  `informacja_ppk` tinyint(1) DEFAULT NULL,
  `rezygnacja_ppk` tinyint(1) DEFAULT NULL,
  `forma_wyplaty` varchar(50) DEFAULT NULL,
  `wynagrodzenie_do_rak_wlasnych` tinyint(1) DEFAULT NULL,
  `zgoda_na_przelew` tinyint(1) DEFAULT NULL,
  `ryzyko_zawodowe` tinyint(1) DEFAULT NULL,
  `oswiadczenie_zus` tinyint(1) DEFAULT NULL,
  `bhp` tinyint(1) DEFAULT NULL,
  `regulamin_pracy` tinyint(1) DEFAULT NULL,
  `zasady_ewidencji_kasa` tinyint(1) DEFAULT NULL,
  `pit2` tinyint(1) DEFAULT NULL,
  `oswiadczenie_art188_kp` tinyint(1) DEFAULT NULL,
  `rodo` tinyint(1) DEFAULT NULL,
  `pora_nocna` tinyint(1) DEFAULT NULL,
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
INSERT INTO `pracownicy` VALUES
('1','Paweł Turek','OCTO','UZ','2026-07-18',1,1,1,1,'2025-07-10',1,'2025-07-13',1,1,'Gotówka',1,1,1,1,1,1,1,1,1,1,1,'asdasd@dashdash','Jolanta','61661611','616161661','Nokia','Orange','2025-07-25'),
('3','Ferenc Arkadio','FUN','','2025-07-26',0,0,1,0,NULL,0,'2025-07-19',0,1,'',0,1,1,0,0,0,0,1,0,0,0,'','','','','','','2025-08-08');
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refuels`
--

LOCK TABLES `refuels` WRITE;
/*!40000 ALTER TABLE `refuels` DISABLE KEYS */;
INSERT INTO `refuels` VALUES
(3,'T14','2025-04-29 16:02:45',30,150,155010,'2025-04-29 14:02:45'),
(5,'T99','2025-07-28 17:17:49',30,300,174000,'2025-07-28 15:17:49'),
(6,'T98','2025-07-28 21:20:10',50,250,174500,'2025-07-28 19:20:10'),
(7,'T108','2025-08-01 11:46:39',5,30,168005,'2025-08-01 09:46:39'),
(8,'T108','2025-08-04 07:47:44',47.06,266.83,89265,'2025-08-04 05:47:44'),
(9,'T108','2025-08-07 10:49:18',21.37,125.01,89665,'2025-08-07 08:49:18'),
(10,'T108','2025-08-19 08:14:19',36.14,200.22,92078,'2025-08-19 06:14:19'),
(11,'T108','2025-08-21 14:25:56',25.49,143.76,92415,'2025-08-21 12:25:56'),
(12,'T108','2025-08-26 08:54:42',52.65,301.68,93371,'2025-08-26 06:54:42'),
(13,'T108','2025-08-28 09:52:40',52.71,292.01,93852,'2025-08-28 07:52:40');
/*!40000 ALTER TABLE `refuels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `serwisy`
--

DROP TABLE IF EXISTS `serwisy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serwisy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rejestracja` varchar(20) NOT NULL,
  `opis` text NOT NULL,
  `koszt` float NOT NULL,
  `zdjecia` text DEFAULT NULL,
  `data` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `rejestracja` (`rejestracja`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serwisy`
--

LOCK TABLES `serwisy` WRITE;
/*!40000 ALTER TABLE `serwisy` DISABLE KEYS */;
INSERT INTO `serwisy` VALUES
(1,'DW9YF48','wymieniony olej i filtry',250,'[]','2025-08-21 12:16:54'),
(2,'DW9YF48','tak',180,'[]','2025-08-21 12:19:25'),
(3,'DW9YF48','fdhhg',300,'[]','2025-08-21 14:56:32'),
(4,'DW9YF48','olej',50,'[]','2025-08-21 14:58:26'),
(5,'DW9YF48','olej i filtry',180,'[]','2025-08-26 10:42:20'),
(6,'DW9YF48','olej wymianka',500,'[]','2025-08-26 14:03:42'),
(7,'DW9YF48','fhff',333,'[]','2025-08-26 14:09:18'),
(8,'DW9YF48','olejeczek',200,'[]','2025-08-27 10:40:36'),
(9,'DW9YF48','wym olejeczekkkk',500,'[]','2025-08-27 14:04:11'),
(10,'DW9YF48','ooooo',900,'[]','2025-08-27 15:15:40'),
(11,'DW9YF48','ttttt',500,'[]','2025-08-28 14:58:26'),
(12,'DW9YF48','qwert',336,'[\"uploads\\/service\\/serv_68b1a94a2bfe8.jpg\"]','2025-08-29 15:21:14'),
(13,'DW9YF48','wym olej',258,'[\"uploads\\/service\\/serv_68b562a04f555.jpg\",\"uploads\\/service\\/serv_68b562a04f85a.jpg\",\"uploads\\/service\\/serv_68b562eeea731.jpg\"]','2025-09-01 11:08:48');
/*!40000 ALTER TABLE `serwisy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `szkody`
--

DROP TABLE IF EXISTS `szkody`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `szkody` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rejestracja` varchar(50) NOT NULL,
  `nr_szkody` varchar(100) NOT NULL,
  `opis` text NOT NULL,
  `status` varchar(50) NOT NULL,
  `zdjecia` text DEFAULT NULL,
  `data` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `rejestracja` (`rejestracja`),
  KEY `nr_szkody` (`nr_szkody`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `szkody`
--

LOCK TABLES `szkody` WRITE;
/*!40000 ALTER TABLE `szkody` DISABLE KEYS */;
INSERT INTO `szkody` VALUES
(1,'DW9YF48','12/78','urwane lusterko','niezgłoszona','[]','2025-08-21 12:18:05'),
(2,'DW9YF48','13','hfdgbh','zgłoszona','[]','2025-08-21 12:19:46'),
(3,'DW9YF48','6566','gdghg','niezgłoszona','[]','2025-08-25 15:18:43'),
(4,'DW9YF48','0909','ca','niezgłoszona','[]','2025-08-26 10:52:01'),
(5,'DW9YF48','56','rozjebany','niezgłoszona','[]','2025-08-27 10:49:16'),
(6,'DW9YF48','9','całkowite uszkodzenie','czeka na rozliczenie','[]','2025-08-27 14:19:39'),
(7,'DW9YF48','121','fhcgg','czeka na wycenę','[]','2025-08-27 15:20:35'),
(8,'DW9YF48','TEST-112510','upload test przez curl','zgłoszona','[\"uploads\\/damages\\/damage_68b171f6ba95f.jpg\"]','2025-08-29 11:25:10'),
(9,'DW9YF48','TEST-manual','upload test reczny','zgłoszona','[\"uploads\\/damages\\/damage_68b1731ac79ac.jpg\"]','2025-08-29 11:30:02'),
(10,'DW9YF48','6345','hhhhhh','niezgłoszona','[\"uploads\\/damages\\/damage_68b1aa43c93f0.jpg\",\"uploads\\/damages\\/damage_68b1aa43cb37e.jpg\"]','2025-08-29 15:25:23'),
(11,'DW9YF48','75455','calka','zgłoszona','[\"uploads\\/damages\\/damage_68b5630b6bfc1.jpg\",\"uploads\\/damages\\/damage_68b5631c31649.jpg\"]','2025-09-01 11:10:35');
/*!40000 ALTER TABLE `szkody` ENABLE KEYS */;
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
(1,'admin','$2y$10$t0Z.HkVHXwvmqVlAm7EU7ucXSeHDIif.TRhobytQWBT/lJVh7E9Dq','admin'),
(2,'flota1','8b046d263fdc049847e891edc83077d8684ea98cba7b7ae72deb64eab14939f1','flotowiec'),
(3,'kierowca1','a89abdeecdb653e7b027d5314df4a02cfbdbf18bf53fd7ef807486af14e1e963','kierowca');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_sessions`
--

DROP TABLE IF EXISTS `work_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_sessions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `driver_id` varchar(50) NOT NULL,
  `vehicle_plate` varchar(20) DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `start_odometer` int(11) NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `end_odometer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `work_sessions_ibfk_1` FOREIGN KEY (`driver_id`) REFERENCES `kierowcy` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_sessions`
--

LOCK TABLES `work_sessions` WRITE;
/*!40000 ALTER TABLE `work_sessions` DISABLE KEYS */;
INSERT INTO `work_sessions` VALUES
(1,'T99','DX23YB','2025-07-30 13:11:32',178001,'2025-07-30 13:12:59',178003),
(2,'T99','DX23YB','2025-07-30 13:22:39',178004,'2025-07-30 13:27:35',179000),
(3,'T99','DX23YB','2025-07-30 13:51:08',179001,'2025-07-30 13:51:25',179005),
(4,'T99','DX23YB','2025-07-30 13:53:18',179008,'2025-07-30 13:53:30',179010),
(5,'T99','DX23YB','2025-07-31 12:25:34',179020,'2025-07-31 13:26:06',180000),
(6,'T99','DX23YB','2025-07-31 13:27:07',180001,NULL,NULL),
(7,'T99','DX23YB','2025-07-31 16:27:53',185000,'2025-07-31 16:28:04',185001),
(8,'T99','DX23YB','2025-07-31 16:36:57',186000,'2025-07-31 16:37:45',186001),
(9,'T108','DKL83517','2025-08-01 11:27:18',2,'2025-08-01 11:31:42',3),
(10,'T108','DKL83517','2025-08-01 11:37:43',88991,'2025-08-01 11:38:21',88991),
(11,'T108','DX23YB','2025-08-01 11:38:54',186001,'2025-08-01 11:48:39',186005),
(12,'T108','DKL83517','2025-08-01 11:56:00',89062,'2025-08-01 18:14:30',89169),
(13,'T108','DKL83517','2025-08-02 12:49:40',89175,'2025-08-02 17:52:00',89263),
(14,'T108','DKL83517','2025-08-04 07:31:51',89263,NULL,NULL),
(15,'T108','DKL83517','2025-08-05 06:52:35',89383,NULL,NULL),
(16,'T99','DW9YF48','2025-08-05 14:38:07',180001,'2025-08-05 14:51:40',182000),
(17,'T99','DX23YB','2025-08-05 14:52:27',186006,NULL,NULL),
(18,'T99','DW9YF48','2025-08-05 14:57:47',183000,'2025-08-05 14:58:31',187000),
(19,'T108','DKL83517','2025-08-05 15:26:22',89464,'2025-08-05 18:16:23',89494),
(20,'T99','DW9YF48','2025-08-05 16:56:16',187001,NULL,NULL),
(21,'T108','DKL83517','2025-08-06 06:53:45',89494,NULL,NULL),
(22,'T99','DW9YF48','2025-08-06 11:54:25',187002,NULL,NULL),
(23,'T99','DW9YF48','2025-08-06 12:15:28',187005,NULL,NULL),
(24,'T99','DW9YF48','2025-08-06 12:19:25',187005,NULL,NULL),
(25,'T99','DW9YF48','2025-08-06 12:30:51',187006,NULL,NULL),
(26,'T99','DW9YF48','2025-08-06 13:11:05',187007,NULL,NULL),
(27,'T99','DW9YF48','2025-08-06 13:34:12',187008,NULL,NULL),
(28,'T99','DW9YF48','2025-08-06 14:40:05',188000,NULL,NULL),
(29,'T108','DKL83517','2025-08-06 15:19:49',89561,'2025-08-06 19:45:31',89601),
(30,'T108','DKL83517','2025-08-07 07:02:01',89601,'2025-08-07 13:57:53',89713),
(31,'T99','DW9YF48','2025-08-07 09:28:32',188001,NULL,NULL),
(32,'T99','DW9YF48','2025-08-07 15:53:29',190000,'2025-08-07 15:57:25',190001),
(33,'T99','DW9YF48','2025-08-07 16:15:14',190003,NULL,NULL),
(34,'T99','DW9YF48','2025-08-07 16:25:49',190005,NULL,NULL),
(35,'T99','DW9YF48','2025-08-11 17:09:00',190006,'2025-08-11 17:09:53',190007),
(36,'T99','DW9YF48','2025-08-12 19:30:41',190007,NULL,NULL),
(37,'T99','DW9YF48','2025-08-12 19:46:08',190009,'2025-08-12 20:10:59',190010),
(38,'T99','DW9YF48','2025-08-14 17:54:59',191000,NULL,NULL),
(39,'T99','DW9YF48','2025-08-18 10:09:39',191001,NULL,NULL),
(40,'T99','DW9YF48','2025-08-18 14:50:22',191002,'2025-08-18 14:52:31',191008),
(41,'T99','DX23YB','2025-08-18 14:53:05',187000,'2025-08-18 14:56:59',190000),
(42,'T99','DW9YF48','2025-08-18 15:01:25',193000,'2025-08-18 15:34:58',200000),
(43,'T108','DKL83517','2025-08-18 17:58:36',91935,'2025-08-19 00:36:59',92071),
(44,'T108','DKL83517','2025-08-19 07:50:18',92071,NULL,NULL),
(45,'T99','DX23YB','2025-08-19 12:32:38',191000,'2025-08-19 12:34:11',191005),
(46,'T50','DW9YF48','2025-08-19 12:35:16',201000,'2025-08-19 12:36:37',202000),
(47,'T50','DW9YF48','2025-08-19 12:38:48',203000,'2025-08-19 12:48:56',203000),
(48,'T50','DW9YF48','2025-08-19 13:31:13',204000,'2025-08-19 13:32:46',204001),
(49,'T99','DX23YB','2025-08-19 13:33:34',195000,'2025-08-19 13:34:36',195000),
(50,'T50','DW9YF48','2025-08-19 13:35:12',204002,NULL,NULL),
(51,'T50','DW9YF48','2025-08-19 13:57:16',204003,NULL,NULL),
(52,'T108','DKL83517','2025-08-20 04:45:06',92158,NULL,NULL),
(53,'T108','DKL83517','2025-08-20 13:55:11',92277,'2025-08-20 17:58:04',92329),
(54,'T50','DW9YF48','2025-08-20 14:54:48',204004,NULL,NULL),
(55,'T50','DW9YF48','2025-08-20 14:58:55',204006,NULL,NULL),
(56,'T50','DW9YF48','2025-08-20 15:01:14',204008,NULL,NULL),
(57,'T108','DKL83517','2025-08-21 04:31:36',92329,NULL,NULL),
(58,'T50','DW9YF48','2025-08-21 09:25:22',205001,NULL,NULL),
(59,'T50','DW9YF48','2025-08-21 10:03:35',205008,NULL,NULL),
(60,'T50','DW9YF48','2025-08-21 10:34:05',205010,NULL,NULL),
(61,'T50','DW9YF48','2025-08-21 11:35:50',205011,NULL,NULL),
(62,'T50','DW9YF48','2025-08-21 12:13:46',205012,NULL,NULL),
(63,'T108','DKL83517','2025-08-21 13:57:02',92405,'2025-08-21 18:03:28',92463),
(64,'T50','DW9YF48','2025-08-21 14:55:51',210000,NULL,NULL),
(65,'T50','DW9YF48','2025-08-21 14:55:52',210000,'2025-08-21 15:00:23',211000),
(66,'T108','DKL83517','2025-08-22 04:34:30',92463,NULL,NULL),
(67,'T108','DKL83517','2025-08-22 12:58:34',92545,'2025-08-22 17:22:43',92649),
(68,'T108','DKL83517','2025-08-23 03:04:12',92649,'2025-08-23 04:32:59',92742),
(69,'T108','DKL83517','2025-08-25 07:13:37',92943,NULL,NULL),
(70,'T50','DW9YF48','2025-08-25 14:53:07',212000,NULL,NULL),
(71,'T108','DKL83517','2025-08-25 18:14:31',93235,'2025-08-25 18:32:19',93240),
(72,'T108','DKL83517','2025-08-26 04:26:23',93240,NULL,NULL),
(73,'T50','DW9YF48','2025-08-26 10:40:55',215000,NULL,NULL),
(74,'T108','DKL83517','2025-08-26 13:52:42',93414,'2025-08-26 18:07:31',93547),
(75,'T50','DW9YF48','2025-08-26 14:02:43',215001,'2025-08-26 14:20:51',216000),
(76,'T108','DKL83517','2025-08-27 06:57:16',93547,NULL,NULL),
(77,'T50','DW9YF48','2025-08-27 10:37:56',216000,NULL,NULL),
(78,'T50','DW9YF48','2025-08-27 14:03:29',217000,'2025-08-27 14:26:58',217000),
(79,'T108','DKL83517','2025-08-27 14:58:43',93673,'2025-08-27 22:36:13',93790),
(80,'T50','DW9YF48','2025-08-27 15:14:38',218000,NULL,NULL),
(81,'T50','DW9YF48','2025-08-27 16:42:17',218000,NULL,NULL),
(82,'T108','DKL83517','2025-08-28 08:06:36',93790,NULL,NULL),
(83,'T50','DW9YF48','2025-08-28 13:23:22',220000,NULL,NULL),
(84,'T50','DW9YF48','2025-08-28 13:44:57',221000,NULL,NULL),
(85,'T50','DW9YF48','2025-08-28 13:58:58',222000,NULL,NULL),
(86,'T50','DW9YF48','2025-08-28 14:13:36',222001,NULL,NULL),
(87,'T50','DW9YF48','2025-08-28 14:24:01',222002,NULL,NULL),
(88,'T50','DW9YF48','2025-08-28 14:34:03',222003,NULL,NULL),
(89,'T50','DW9YF48','2025-08-28 14:57:43',222003,NULL,NULL),
(90,'T108','DKL83517','2025-08-28 17:41:41',93970,'2025-08-28 17:49:51',93977),
(91,'T108','DKL83517','2025-08-29 07:14:59',93977,NULL,NULL),
(92,'T108','DKL83517','2025-08-29 11:27:55',94020,'2025-08-29 16:14:54',94060),
(93,'T50','DW9YF48','2025-08-29 15:20:33',222018,NULL,NULL),
(94,'T108','DKL83517','2025-08-31 13:32:22',94178,'2025-08-31 17:56:13',94180),
(95,'T108','DKL83517','2025-09-01 04:37:24',94380,NULL,NULL),
(96,'T50','DW9YF48','2025-09-01 11:08:13',222019,NULL,NULL),
(97,'T50','DW9YF48','2025-09-01 12:26:37',222020,'2025-09-01 12:29:07',222021),
(98,'T50','DW9YF48','2025-09-01 12:39:02',222022,'2025-09-01 12:39:11',222023),
(99,'T50','DW9YF48','2025-09-01 14:02:02',222030,'2025-09-01 14:02:39',222031),
(100,'T50','DW9YF48','2025-09-01 14:09:02',222032,'2025-09-01 14:11:02',222035);
/*!40000 ALTER TABLE `work_sessions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-01 15:29:12
