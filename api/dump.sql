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
-- Table structure for table `app_error_logs`
--

DROP TABLE IF EXISTS `app_error_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_error_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `source` varchar(32) NOT NULL,
  `level` varchar(16) NOT NULL DEFAULT 'ERROR',
  `summary` varchar(255) NOT NULL,
  `message` text DEFAULT NULL,
  `stacktrace` longtext DEFAULT NULL,
  `driver_id` varchar(64) DEFAULT NULL,
  `license_plate` varchar(32) DEFAULT NULL,
  `app_version` varchar(32) DEFAULT NULL,
  `device_id` varchar(64) DEFAULT NULL,
  `metadata` longtext DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_app_error_logs_driver` (`driver_id`),
  KEY `idx_app_error_logs_plate` (`license_plate`),
  KEY `idx_app_error_logs_created` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_error_logs`
--

LOCK TABLES `app_error_logs` WRITE;
/*!40000 ALTER TABLE `app_error_logs` DISABLE KEYS */;
INSERT INTO `app_error_logs` VALUES
(1,'mobile','WARN','Odrzucono odświeżenie tokenu','Serwer zwrócił HTTP 401 dla urządzenia unknown',NULL,NULL,NULL,'1.1.2','eeb63091d2636dfe',NULL,'2025-10-22 12:54:43'),
(2,'mobile','WARN','Odrzucono odświeżenie tokenu','Serwer zwrócił HTTP 401 dla urządzenia unknown',NULL,'test','DW1','1.1.2','eeb63091d2636dfe',NULL,'2025-10-22 15:42:17'),
(3,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:51:40'),
(4,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:51:40'),
(5,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-22 16:51:41'),
(6,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:51:58'),
(7,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:51:58'),
(8,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-22 16:51:58'),
(9,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:52:10'),
(10,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:52:10'),
(11,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-22 16:52:10'),
(12,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:52:28'),
(13,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 16:52:28'),
(14,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-22 16:52:28'),
(15,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 17:28:31'),
(16,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-22 17:28:32'),
(17,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-22 17:28:32'),
(18,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 10:01:17'),
(19,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 10:01:17'),
(20,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 10:01:17'),
(21,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 15:59:55'),
(22,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 15:59:55'),
(23,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 15:59:55'),
(24,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:04:01'),
(25,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test','DW','1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:04:01'),
(26,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test','DW','1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:04:01'),
(27,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:09:34'),
(28,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:09:34'),
(29,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:35'),
(30,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:35'),
(31,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:10:35'),
(32,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:41'),
(33,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:41'),
(34,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:10:41'),
(35,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:45'),
(36,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:45'),
(37,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:10:45'),
(38,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:46'),
(39,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:10:46'),
(40,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:10:46'),
(41,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:00'),
(42,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:00'),
(43,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:11:00'),
(44,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:01'),
(45,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:01'),
(46,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:11:02'),
(47,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:09'),
(48,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:10'),
(49,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:11:10'),
(50,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:51'),
(51,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:11:51'),
(52,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:11:51'),
(53,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:12:28'),
(54,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:12:28'),
(55,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:12:28'),
(56,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:13:18'),
(57,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:13:19'),
(58,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:13:19'),
(59,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:14:46'),
(60,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:14:46'),
(61,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:14:46'),
(62,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:14:55'),
(63,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-23 16:14:55'),
(64,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-23 16:14:55'),
(65,'mobile','WARN','Nieoczekiwana odpowiedź odświeżenia tokenu','Kod 404 Not Found',NULL,'test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-24 09:32:42'),
(66,'mobile','ERROR','Błąd sieci podczas odświeżania tokenu','Refresh token request failed with HTTP 404','java.io.IOException: Refresh token request failed with HTTP 404\n	at com.partner.taxi.ApiClient.refreshToken(ApiClient.kt:152)\n	at com.partner.taxi.ApiClient.unauthorizedInterceptor$lambda$8(ApiClient.kt:44)\n	at com.partner.taxi.ApiClient.$r8$lambda$qA_KXSgo0vHlCNINe58vablllzg(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda1.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at com.partner.taxi.ApiClient.authInterceptor$lambda$3(ApiClient.kt:36)\n	at com.partner.taxi.ApiClient.$r8$lambda$kqQhDfdtA_cdsPC_o7qWK3NjtQE(Unknown Source:0)\n	at com.partner.taxi.ApiClient$$ExternalSyntheticLambda0.intercept(D8$$SyntheticClass:0)\n	at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.kt:109)\n	at okhttp3.internal.connection.RealCall.getResponseWithInterceptorChain$okhttp(RealCall.kt:201)\n	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:517)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1156)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:651)\n	at java.lang.Thread.run(Thread.java:1119)','test',NULL,'1.1.3','97fe62eefa683015',NULL,'2025-10-24 09:32:42'),
(67,'mobile','WARN','Problem z połączeniem z API','Nie udało się odświeżyć tokenu z powodu problemu z siecią.',NULL,'test',NULL,'1.1.3','97fe62eefa683015','{\"exception\":\"com.partner.taxi.TokenRefreshException\",\"cause\":\"java.io.IOException\",\"thread\":\"main\"}','2025-10-24 09:32:42'),
(68,'mobile','WARN','Odrzucono odświeżenie tokenu','Serwer zwrócił HTTP 401 dla urządzenia unknown',NULL,NULL,NULL,'1.1.3','eeb63091d2636dfe',NULL,'2025-10-24 09:33:01');
/*!40000 ALTER TABLE `app_error_logs` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaboration_terms`
--

LOCK TABLES `collaboration_terms` WRITE;
/*!40000 ALTER TABLE `collaboration_terms` DISABLE KEYS */;
INSERT INTO `collaboration_terms` VALUES
(37,'T14','percentTurnover','80'),
(38,'T14','fuelCost','0'),
(39,'T14','cardCommission','3'),
(40,'T14','partnerCommission','10'),
(41,'T14','boltCommission','0'),
(42,'T14','settlementLimit','1000'),
(43,'T1','percentTurnover','80'),
(44,'T1','fuelCost','0'),
(45,'T1','cardCommission','3'),
(46,'T1','partnerCommission','10'),
(47,'T1','boltCommission','0'),
(48,'T1','settlementLimit','1000'),
(49,'T108','percentTurnover','40'),
(50,'T108','fuelCost','0'),
(51,'T108','cardCommission','3'),
(52,'T108','partnerCommission','0'),
(53,'T108','boltCommission','0'),
(54,'T108','settlementLimit','3000'),
(55,'T21','percentTurnover','40'),
(56,'T21','fuelCost','0'),
(57,'T21','cardCommission','3'),
(58,'T21','partnerCommission','0'),
(59,'T21','boltCommission','0'),
(60,'T21','settlementLimit','1000'),
(67,'test','percentTurnover','40'),
(68,'test','fuelCost','0'),
(69,'test','cardCommission','3'),
(70,'test','partnerCommission','20'),
(71,'test','boltCommission','0'),
(72,'test','settlementLimit','1000'),
(73,'T22','percentTurnover','40'),
(74,'T22','fuelCost','0'),
(75,'T22','cardCommission','3'),
(76,'T22','partnerCommission','0'),
(77,'T22','boltCommission','0'),
(78,'T22','settlementLimit','1000'),
(79,'test1','percentTurnover','40'),
(80,'test1','fuelCost','0'),
(81,'test1','cardCommission','3'),
(82,'test1','partnerCommission','20'),
(83,'test1','boltCommission','20'),
(84,'test1','settlementLimit','500');
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
  `counter_type` enum('saldo','voucher_current','voucher_previous') NOT NULL DEFAULT 'saldo',
  `opis` text DEFAULT NULL,
  `data` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historia_salda`
--

LOCK TABLES `historia_salda` WRITE;
/*!40000 ALTER TABLE `historia_salda` DISABLE KEYS */;
INSERT INTO `historia_salda` VALUES
(1,'T99',100.00,40.00,'Premia','saldo',NULL,'2025-09-02 19:23:07'),
(2,'T99',78.00,118.00,'Premia','saldo',NULL,'2025-09-02 19:24:38'),
(3,'T99',-90.00,28.00,'Rozliczenie','saldo',NULL,'2025-09-02 19:25:07'),
(4,'T99',78.00,106.00,'Rozliczenie','saldo',NULL,'2025-09-02 19:26:17'),
(5,'T99',77.00,183.00,'Rozliczenie','saldo',NULL,'2025-09-02 19:26:53'),
(6,'T99',100.00,283.00,'Rozliczenie','saldo',NULL,'2025-09-02 19:34:11'),
(7,'T99',232.00,515.00,'Rozliczenie','saldo',NULL,'2025-09-02 20:14:44'),
(8,'T99',111.00,626.00,'Premia','saldo',NULL,'2025-09-02 20:32:30'),
(9,'T99',567.00,1193.00,'Rozliczenie','saldo',NULL,'2025-09-02 20:45:24'),
(10,'T99',12.00,1205.00,'Kara','saldo',NULL,'2025-09-02 21:14:36'),
(11,'T99',100.00,1305.00,'Rozliczenie','saldo',NULL,'2025-09-03 09:47:49'),
(12,'T99',-90.00,1215.00,'Kara','saldo',NULL,'2025-09-03 09:48:15'),
(13,'T99',32.00,1247.00,'Rozliczenie','saldo',NULL,'2025-09-03 09:49:52'),
(14,'T1',100.00,100.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:01:18'),
(15,'T99',100.00,1347.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:08:48'),
(16,'T99',-10.00,1337.00,'Kara','saldo',NULL,'2025-09-03 10:09:42'),
(17,'T99',-100.00,1237.00,'Kara','saldo',NULL,'2025-09-03 10:10:12'),
(18,'T99',10.00,1247.00,'Kara','saldo',NULL,'2025-09-03 10:10:43'),
(19,'T99',111.00,1358.00,'Premia','saldo',NULL,'2025-09-03 10:11:26'),
(20,'T99',100.00,1458.00,'Premia','saldo',NULL,'2025-09-03 10:11:53'),
(21,'T99',-100.00,1358.00,'Kara','saldo',NULL,'2025-09-03 10:14:13'),
(22,'T99',100.00,1458.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:14:53'),
(23,'T99',100.00,1558.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:15:12'),
(24,'T1',22.00,122.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:16:35'),
(25,'T1',33.00,155.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:17:11'),
(26,'T1',100.00,255.00,'Kara','saldo',NULL,'2025-09-03 10:50:25'),
(27,'T1',111.00,366.00,'Rozliczenie','saldo',NULL,'2025-09-03 10:59:52'),
(28,'T1',1.00,367.00,'Rozliczenie','saldo',NULL,'2025-09-03 11:02:37'),
(29,'T1',1.00,368.00,'Rozliczenie','saldo',NULL,'2025-09-03 11:03:41'),
(30,'T1',22.00,390.00,'Rozliczenie','saldo',NULL,'2025-09-03 11:03:56'),
(31,'T99',33.00,1591.00,'Rozliczenie','saldo',NULL,'2025-09-03 11:05:13'),
(32,'T99',333.00,1924.00,'Rozliczenie','saldo',NULL,'2025-09-03 11:05:27'),
(33,'T108',2328.00,2328.00,'Rozliczenie','saldo',NULL,'2025-09-04 09:42:19'),
(34,'T108',933.00,3261.00,'Rozliczenie','saldo',NULL,'2025-09-04 09:42:56'),
(35,'T108',0.37,3261.37,'Rozliczenie','saldo',NULL,'2025-09-04 09:43:30'),
(36,'T108',0.18,3261.55,'Rozliczenie','saldo',NULL,'2025-09-04 09:43:54'),
(37,'T108',1600.00,4861.55,'podstawa IX','saldo',NULL,'2025-09-04 09:44:54'),
(38,'T108',-207.00,4654.55,'rata 11/20','saldo',NULL,'2025-09-04 09:45:23'),
(39,'T108',-2.00,4653.34,'Kara','saldo',NULL,'2025-09-04 09:50:56'),
(40,'T108',1.21,4654.55,'Premia','saldo',NULL,'2025-09-04 09:52:31'),
(41,'T108',320.00,5048.15,'dniówka za Leszno 31-08','saldo',NULL,'2025-09-04 14:22:56'),
(42,'T1',-194.00,0.00,'Kara','saldo',NULL,'2025-09-04 14:52:31'),
(43,'T1',45.00,45.00,'jesteś zjebany','saldo',NULL,'2025-09-04 14:53:02'),
(44,'T1',-45.00,0.00,'Rozliczenie','saldo',NULL,'2025-09-04 14:53:31'),
(45,'T21',-60.00,-36.72,'Kara','saldo',NULL,'2025-09-04 15:15:19'),
(46,'T21',36.72,0.00,'bo tak','saldo',NULL,'2025-09-04 15:17:37'),
(47,'T21',1195.21,1195.21,'Rozliczenie','saldo',NULL,'2025-09-04 15:19:30'),
(48,'T21',424.26,1619.47,'Rozliczenie','saldo',NULL,'2025-09-04 15:19:50'),
(49,'T21',-1400.00,219.47,'Rozliczenie','saldo',NULL,'2025-09-04 15:21:07'),
(50,'T15',-13.58,0.00,'Rozliczenie','saldo',NULL,'2025-09-05 16:28:01'),
(51,'T15',-41.03,-41.03,'Rozliczenie','saldo',NULL,'2025-09-05 16:28:35'),
(52,'test',50.00,166.40,'Rozliczenie','saldo',NULL,'2025-10-20 10:34:39'),
(53,'T22',543.64,2653.64,'Rozliczenie','saldo',NULL,'2025-10-20 10:48:21'),
(54,'T22',-2000.00,653.64,'Rozliczenie','saldo',NULL,'2025-10-20 10:52:40'),
(55,'test',100.67,305.87,'Rozliczenie','saldo',NULL,'2025-10-22 12:31:17'),
(56,'test',100.03,405.90,'Rozliczenie','saldo',NULL,'2025-10-22 12:49:56');
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inwentaryzacje`
--

LOCK TABLES `inwentaryzacje` WRITE;
/*!40000 ALTER TABLE `inwentaryzacje` DISABLE KEYS */;
INSERT INTO `inwentaryzacje` VALUES
(5,'DW3NC94',1,1,'uploads/inventory/front_68b856eb355b5.jpg','2025-09-03 16:55:39','uploads/inventory/back_68b856eb3587c.jpg','uploads/inventory/left_68b856eb35a55.jpg','uploads/inventory/right_68b856eb35c4f.jpg',0,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,0,NULL,'T14'),
(6,'DKL83517',95149,1,'uploads/inventory/front_68b942b38e473.jpg','2025-09-04 09:41:39','uploads/inventory/back_68b942b38e8a8.jpg','uploads/inventory/left_68b942b38eac0.jpg','uploads/inventory/right_68b942b38ef59.jpg',8,NULL,NULL,NULL,NULL,1,0,0,0,1,1,1,1,1,NULL,'T108'),
(7,'TEST',100,1,'uploads/inventory/front_68b986eb78bad.jpg','2025-09-04 14:32:43','uploads/inventory/back_68b986eb78dde.jpg','uploads/inventory/left_68b986eb78f99.jpg','uploads/inventory/right_68b986eb7911a.jpg',5,NULL,NULL,NULL,NULL,0,0,0,0,0,0,0,0,1,NULL,'T1'),
(8,'DW8YK67',119511,1,'uploads/inventory/front_68b99022d1139.jpg','2025-09-04 15:12:02','uploads/inventory/back_68b99022d13a5.jpg','uploads/inventory/left_68b99022d15d8.jpg','uploads/inventory/right_68b99022d17a8.jpg',0,NULL,NULL,NULL,NULL,1,1,0,0,1,1,1,1,0,NULL,'T21'),
(9,'DW8YK66',1,1,'uploads/inventory/front_68baf29bb11c2.jpg','2025-09-05 16:24:27','uploads/inventory/back_68baf29bb15e1.jpg','uploads/inventory/left_68baf29bb19ef.jpg','uploads/inventory/right_68baf29bb1d83.jpg',5,NULL,NULL,NULL,NULL,1,1,1,1,1,1,1,1,1,NULL,'T15'),
(10,'DW',1,1,'uploads/inventory/front_68bead42b5b83.jpg','2025-09-08 12:17:38','uploads/inventory/back_68bead42b5e0e.jpg','uploads/inventory/left_68bead42b6022.jpg','uploads/inventory/right_68bead42b6240.jpg',0,NULL,NULL,NULL,NULL,1,1,0,1,0,0,0,0,0,NULL,'test'),
(11,'DW6WF39',259126,1,'uploads/inventory/front_68f5f6f60a1ea.jpg','2025-10-20 10:46:46','uploads/inventory/back_68f5f6f60a5d8.jpg','uploads/inventory/left_68f5f6f60a967.jpg','uploads/inventory/right_68f5f6f60acca.jpg',5,NULL,NULL,NULL,NULL,1,1,0,0,1,1,1,1,1,NULL,'T22'),
(12,'DW1',2,1,'uploads/inventory/front_68f8dfe3cf2d9.jpg','2025-10-22 15:45:07','uploads/inventory/back_68f8dfe3cf57d.jpg','uploads/inventory/left_68f8dfe3cf792.jpg','uploads/inventory/right_68f8dfe3cf9a8.jpg',5,NULL,NULL,NULL,NULL,1,1,1,1,1,1,1,1,1,NULL,'test'),
(13,'DW',39,1,'uploads/inventory/front_68f8e0ce457fd.jpg','2025-10-22 15:49:02','uploads/inventory/back_68f8e0ce45aa2.jpg','uploads/inventory/left_68f8e0ce45ca6.jpg','uploads/inventory/right_68f8e0ce45e6a.jpg',1,NULL,NULL,NULL,NULL,1,0,1,0,1,0,0,1,1,'brud','test1'),
(14,'DW',40,1,'uploads/inventory/front_68fa35adbbfdd.jpg','2025-10-23 16:03:25','uploads/inventory/back_68fa35adbc2dd.jpg','uploads/inventory/left_68fa35adbc57c.jpg','uploads/inventory/right_68fa35adbc7c8.jpg',6,NULL,NULL,NULL,NULL,1,1,1,1,1,1,1,1,1,NULL,'test');
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
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jwt_tokens`
--

LOCK TABLES `jwt_tokens` WRITE;
/*!40000 ALTER TABLE `jwt_tokens` DISABLE KEYS */;
INSERT INTO `jwt_tokens` VALUES
(1,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY3MzQyOTgsImV4cCI6MTc1Njc2MzA5OCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ._jv9pjCsx4ry41TPTY7uCkytJflWHyYOoaFK4Xx4qOk','1','admin_panel','2025-09-01 21:44:58'),
(5,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY3MzU4NjAsImV4cCI6MTc1Njc2NDY2MCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.0qJtoW9dTOkoxiKhcoYsKRNEvE_Wdjse0LIGUw3qaCQ','1','admin_panel','2025-09-01 22:11:00'),
(6,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4MzMyODUsImV4cCI6MTc1Njg2MjA4NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.ryShNhOWIAOp6Jra1DCPlhRmKPRnEeHc_txbZB1NH7U','1','admin_panel','2025-09-03 01:14:45'),
(10,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4MzYwNzUsImV4cCI6MTc1Njg2NDg3NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.UPkDCfz9GzOLHf4Oysom7s0d-VNom0onv8QogOxMdj0','1','admin_panel','2025-09-03 02:01:15'),
(11,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4MzY4NjMsImV4cCI6MTc1Njg2NTY2MywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.ezyJ57OMtmqOhMu9GTZ81jT_jBgscVYPW7RRP6v_Xhc','1','admin_panel','2025-09-03 02:14:23'),
(13,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4Mzc4OTUsImV4cCI6MTc1Njg2NjY5NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.XRrGXrIXOV_tUXYTbsmyswdjF3ZfIFkzp_dtmXkybDI','1','admin_panel','2025-09-03 02:31:35'),
(14,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4Mzg3MTIsImV4cCI6MTc1Njg2NzUxMiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.CF005FWF22_f_JE3ZL717TgtWVzVT1_1V1o0Yc5WnyY','1','admin_panel','2025-09-03 02:45:12'),
(15,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4NDA0NTUsImV4cCI6MTc1Njg2OTI1NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.IEyEoOmxsfddQUSsgPAET8HBK0-EHX9yhrVtNwEfMPQ','1','admin_panel','2025-09-03 03:14:15'),
(16,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4NDExMzYsImV4cCI6MTc1Njg2OTkzNiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.dDWefQgaltxk8HNITF-FumVu8o5ZIzZBZnbDijDhtdw','1','admin_panel','2025-09-03 03:25:36'),
(23,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4ODU2NTYsImV4cCI6MTc1NjkxNDQ1NiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.uBvYeCe2Px-YT246u3z5jbPdPxnsDLcRalGEhOMwpz0','1','admin_panel','2025-09-03 15:47:36'),
(25,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4ODY4ODUsImV4cCI6MTc1NjkxNTY4NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.XL_Pr2TSq5WrfynQ5bZ6Aju_osgkstezdVuAoBOWfgk','1','admin_panel','2025-09-03 16:08:05'),
(30,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4ODgxNzgsImV4cCI6MTc1NjkxNjk3OCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.6puAZTxCuR2iOI6XX4HmT7TunJVQf5t6V5qgRpxwOzI','1','admin_panel','2025-09-03 16:29:38'),
(36,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4ODk5NzIsImV4cCI6MTc1NjkxODc3MiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.6L6J56LgrzT-PqsMwxuJ0f3tLGXaYdWs1GZiAIu2vOU','1','admin_panel','2025-09-03 16:59:32'),
(39,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4OTAzMDIsImV4cCI6MTc1NjkxOTEwMiwidXNlcl9pZCI6IlQ5OSIsInJvbGUiOiJmbG90b3dpZWMiLCJkZXZpY2VfaWQiOiJlZWI2MzA5MWQyNjM2ZGZlIn0.E3a49BD5eJVdsESvX9Fhjl7u6DiMM7RxS7SnUoVJFQg','T99','eeb63091d2636dfe','2025-09-03 17:05:02'),
(40,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4OTA4MzIsImV4cCI6MTc1NjkxOTYzMiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.ywKV_l7paaeObiQl1cxUNJWRPCWVW7w6NnIxveIUN_Y','1','admin_panel','2025-09-03 17:13:52'),
(42,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4OTE2MjAsImV4cCI6MTc1NjkyMDQyMCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.JFDJeZdsn6Cjigb8XQGPPPwDXeElv2zDU21swSg6qJU','1','admin_panel','2025-09-03 17:27:00'),
(43,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4OTI5MzIsImV4cCI6MTc1NjkyMTczMiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.FMEvPRgUL_58eRwQuOjitOxj8iZwJadzPqZodwYNem8','1','admin_panel','2025-09-03 17:48:52'),
(44,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY4OTMxMTYsImV4cCI6MTc1NjkyMTkxNiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.RxNpTGz6H3InHSLNogLUtSDotESx6u-IJO-SXcPnhAQ','1','admin_panel','2025-09-03 17:51:56'),
(45,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5MDI1MDUsImV4cCI6MTc1NjkzMTMwNSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.hAaK11wngYx10ZiUnjibxlwGVnB4F617zmFujzk47Oc','1','admin_panel','2025-09-03 20:28:25'),
(46,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5MDk1NDIsImV4cCI6MTc1NjkzODM0MiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.8EepvAk9vDTB1wPM5Cgkv7-Z6aea6WW0NvsQtRUfZ8E','1','admin_panel','2025-09-03 22:25:42'),
(47,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5MDk3MTYsImV4cCI6MTc1NjkzODUxNiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.g__gZ4Y1idbdCmh9_OU3KUwW6Vt6_Mm9jCxcKRdMpTY','1','admin_panel','2025-09-03 22:28:36'),
(48,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5MTAyOTcsImV4cCI6MTc1NjkzOTA5NywidXNlcl9pZCI6IlQxNCIsInJvbGUiOiJmbG90b3dpZWMiLCJkZXZpY2VfaWQiOiJhOWVlNzc2YmFkMzQyMDYzIn0.m4XY475B1tLi-8l30Qhxl3CCXC7T6JdVuTkbfAR8xe0','T14','a9ee776bad342063','2025-09-03 22:38:17'),
(49,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5NzA1NzUsImV4cCI6MTc1Njk5OTM3NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.NPiICR6gWYcKn2D0G2Yz33iWP_5bgndb3BqgRAn3d8U','1','admin_panel','2025-09-04 15:22:55'),
(50,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5NzEyMTcsImV4cCI6MTc1NzAwMDAxNywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.a3l1O7odagXYR-Uo3xCi-s3WVilr8_wSqhCkGSV8vwg','1','admin_panel','2025-09-04 15:33:37'),
(51,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5NzE2MjAsImV4cCI6MTc1NzAwMDQyMCwidXNlcl9pZCI6IlQxMDgiLCJyb2xlIjoia2llcm93Y2EiLCJkZXZpY2VfaWQiOiJiZWRlZWI0YmM2YTg2YTQ1In0.3ohpIQjpIkr6PRn5401lm2pbAoAI7musViZFUjoF3G4','T108','bedeeb4bc6a86a45','2025-09-04 15:40:20'),
(52,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5ODg5OTksImV4cCI6MTc1NzAxNzc5OSwidXNlcl9pZCI6IlQxIiwicm9sZSI6ImZsb3Rvd2llYyIsImRldmljZV9pZCI6IjdhZDNlNzYyMTY0NjgyYWMifQ.oRrHyDbczIt8E1mJCa6AxZayRTkbSrGcfNJWfbJ4bro','T1','7ad3e762164682ac','2025-09-04 20:29:59'),
(53,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTY5OTEzMDQsImV4cCI6MTc1NzAyMDEwNCwidXNlcl9pZCI6IlQyMSIsInJvbGUiOiJraWVyb3djYSIsImRldmljZV9pZCI6ImExZDYwNzY1NmNjYmNkNTIifQ.i5ocAI9BD3UBa4jywK49TD1cGUpA4Q-AfpDNLNodxiE','T21','a1d607656ccbcd52','2025-09-04 21:08:24'),
(54,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTcwODIwMjUsImV4cCI6MTc1NzExMDgyNSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ._rKI1RgeKVwiya9GfA3aN4LzTy-D9dG5DAO_biL4yX8','1','admin_panel','2025-09-05 22:20:25'),
(55,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTcwODIxNzYsImV4cCI6MTc1NzExMDk3NiwidXNlcl9pZCI6IlQxNSIsInJvbGUiOiJraWVyb3djYSIsImRldmljZV9pZCI6IjAyZTA1MjMyMmIyOTgxNjYifQ.V5oKykvjyo0Zw6DfHQd9rAiu9o_R63VN7n22OmBQZnU','T15','02e052322b298166','2025-09-05 22:22:56'),
(56,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTczMTY0NTAsImV4cCI6MTc1NzM0NTI1MCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.dqsuRfOOQsns3IrKDTeYV4TQ7Xxd8s2fUQztfPxKeGI','1','admin_panel','2025-09-08 15:27:30'),
(57,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTczMjY1MTEsImV4cCI6MTc1NzM1NTMxMSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.hoYnLYUdhplVP2F4I3nlN0DZd2rONuYhOloN5MTnHSI','1','admin_panel','2025-09-08 18:15:11'),
(59,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTczMzk1OTIsImV4cCI6MTc1NzM2ODM5MiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.q_hY-vSHoaxq3of9JP-kr5E3UA3RLjbx0NgvK5g13s8','1','admin_panel','2025-09-08 21:53:12'),
(60,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTczNDAyODIsImV4cCI6MTc1NzM2OTA4MiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.LM99PRT3NG-3hJYCIpidEujkLcaoRFiwsXsxtbOXhHI','1','admin_panel','2025-09-08 22:04:42'),
(62,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTgwMDYxMTQsImV4cCI6MTc1ODAzNDkxNCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.WPNyIMdifZlgGIO2PhjPnCi3H4NBZUA1_upPx1L3IWY','1','admin_panel','2025-09-16 15:01:54'),
(63,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NTk4NDc2ODAsImV4cCI6MjA3NTIwNzY4MCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.1M_5B-y-JrKBSl8ZJAB0SUFgJqQl4L43NTP7jF-XSls','1','admin_panel','2035-10-05 14:34:40'),
(65,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA2MDY1NDUsImV4cCI6MjA3NTk2NjU0NSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.Sq3MMm3wjFO4jjav9NPE07HmdkZPVeTi56AhN1So7VY','1','admin_panel','2035-10-14 09:22:25'),
(69,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA5NDkyMjcsImV4cCI6MjA3NjMwOTIyNywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.Fxtxvw5TcLiatvrRz_z308SI_PV70_oHaSoPHQbfoQI','1','admin_panel','2035-10-18 08:33:47'),
(70,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA5NDk1MjksImV4cCI6MjA3NjMwOTUyOSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.2qW67g1pXnnOdCR-4qXaHcpSXFyqA1krweLXQxnu1IA','1','admin_panel','2035-10-18 08:38:49'),
(71,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA5NDk3NTYsImV4cCI6MjA3NjMwOTc1NiwidXNlcl9pZCI6IlQyMiIsInJvbGUiOiJraWVyb3djYSIsImRldmljZV9pZCI6IjM3YzBiNzUwOGRmNTllYmEifQ.d-IpW5-LkJKAJ2b454DG7OlpnXs2sIyo0BvDcqnM5dU','T22','37c0b7508df59eba','2035-10-18 08:42:36'),
(72,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA5NTA1NjMsImV4cCI6MjA3NjMxMDU2MywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.lRUuIDmg8CVmroCIgxqbLwKhhOMCHsJHz3IacJzWMIg','1','admin_panel','2035-10-18 08:56:03'),
(73,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjA5NjEzMzQsImV4cCI6MjA3NjMyMTMzNCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.TsFyb81R5vFyUUvLqljl0IiVZsq_2NYcLtDq-PWjobc','1','admin_panel','2035-10-18 11:55:34'),
(74,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEwNDQ3MDYsImV4cCI6MjA3NjQwNDcwNiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ._TbJutUki-sFR8pntFQo81SU_flvjJsl8VYcmTyEIxk','1','admin_panel','2035-10-19 11:05:06'),
(75,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMjkwMzksImV4cCI6MjA3NjQ4OTAzOSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.7Zu-lXljQsFck7ueBzoY-GJfbZ3EU8VlRmMSmLjpus4','1','admin_panel','2035-10-20 10:30:39'),
(76,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMjkyMTgsImV4cCI6MjA3NjQ4OTIxOCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.CfQ4xHSXxjFyzeKPqlIzygXMmbpmfOjTZZUUod2VKHM','1','admin_panel','2035-10-20 10:33:38'),
(77,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMzAwMzcsImV4cCI6MjA3NjQ5MDAzNywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.cZO33mTG907aI5kQ07ym7qxB5TdezYHfog3jVMAU_PI','1','admin_panel','2035-10-20 10:47:17'),
(79,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMzA4NjgsImV4cCI6MjA3NjQ5MDg2OCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.UfDrn5pxGCw30vlAfhfALdxA66g0dMvDKSoovBHRVdY','1','admin_panel','2035-10-20 11:01:08'),
(80,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMzE0MTEsImV4cCI6MjA3NjQ5MTQxMSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.Y7s44dIRYWHGXsWu8OxZ6E1GRIONJqSYrg5S9JSfoR8','1','admin_panel','2035-10-20 11:10:11'),
(81,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMzg2ODgsImV4cCI6MjA3NjQ5ODY4OCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.nUa0lL_H4sFWAQqWMDhj9Yk6HOZ7vur5eNrs-LHd5TE','1','admin_panel','2035-10-20 13:11:28'),
(82,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExMzg5ODcsImV4cCI6MjA3NjQ5ODk4NywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.Tw7GzKDa45VA3ejZ_zWo08ckNrg1pXX9JpSiNdAteeo','1','admin_panel','2035-10-20 13:16:27'),
(85,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExNDM4MjEsImV4cCI6MjA3NjUwMzgyMSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.dnYWxYLnLzSGlRhs6cA0Om7hTtgCt18Ib9ONmlf8c6o','1','admin_panel','2035-10-20 14:37:01'),
(86,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExNDM5MzksImV4cCI6MjA3NjUwMzkzOSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.cf6QKB3HHo6egfSf4Bs0QsSxyXzfDT_gKzH3fsx5pFM','1','admin_panel','2035-10-20 14:38:59'),
(87,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExNDU3MDYsImV4cCI6MjA3NjUwNTcwNiwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.OtG2SV6MLJm3g_D7VJCdKN6uO-xYxqKBX3v7Hpvb46o','1','admin_panel','2035-10-20 15:08:26'),
(88,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExNDYyMDksImV4cCI6MjA3NjUwNjIwOSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.rTkJq-_p69FNihC4UtJ43aty-XzcdMemIxk-9711J8Y','1','admin_panel','2035-10-20 15:16:49'),
(89,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjExNDYzNTQsImV4cCI6MjA3NjUwNjM1NCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.sSDk89VfcT8oTY26nt09eG8EER-o104v1BH8QAaBfhw','1','admin_panel','2035-10-20 15:19:14'),
(90,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEyMjc4MTAsImV4cCI6MjA3NjU4NzgxMCwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.OJkXNgrC0g4Qjhq0vf5b1GfWToV4wFBCdPUV9TO4ePw','1','admin_panel','2035-10-21 13:56:50'),
(92,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEyOTA3OTMsImV4cCI6MjA3NjY1MDc5MywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.B5j9d3l2qAOAIpc_wBMSW-P3ugkkBSMheC2tiVPPFeQ','1','admin_panel','2035-10-22 07:26:33'),
(93,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEyOTA4NTcsImV4cCI6MjA3NjY1MDg1NywidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.jDQWKhVdCa6-517jLQqxTYDCDzJBm2Y-wA2v9FDKYtA','1','admin_panel','2035-10-22 07:27:37'),
(94,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEyOTExOTAsImV4cCI6MjA3NjY1MTE5MCwidXNlcl9pZCI6InRlc3QiLCJyb2xlIjoiZmxvdG93aWVjIiwiZGV2aWNlX2lkIjoiZWViNjMwOTFkMjYzNmRmZSJ9.elp4CIVEoP85hjRftBNZP73Kwd4PKhzfoPcaTzPUOyE','test','eeb63091d2636dfe','2035-10-22 07:33:10'),
(95,'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NjEzMDE2MjUsImV4cCI6MjA3NjY2MTYyNSwidXNlcl9pZCI6MSwicm9sZSI6ImFkbWluIiwiZGV2aWNlX2lkIjoiYWRtaW5fcGFuZWwifQ.Ug1JowT9AlR3pnU0-kEF_a3lvHSsq-YdS64GzxiDUWU','1','admin_panel','2035-10-22 10:27:05');
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
  `voucher_current_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `voucher_current_month` varchar(7) DEFAULT NULL,
  `voucher_previous_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `voucher_previous_month` varchar(7) DEFAULT NULL,
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
('T1','Arkadiusz','Ferenc',0.00,0.00,'2025-10',0.00,'2025-09','$2y$10$1Ba0skCLXWf/5FsAQ7M9ZO8YwtEcBUfqMRWvoW.ShmITNqXourrly','flotowiec','firma','2025-09-04 07:24:21','aktywny','TEST','fbK6e1iNTBWZYgXJ7ia98t:APA91bFL0m5aBTcEtDGXzsRyybeg5FNURQiZvLJnhhfYNPXhoDhYqJodr5xtmOjZluH0Fgnu_MtyafvvLWDLgs6iE2i2lntgUHOuo8Ido9n1gigerxD5EiU'),
('T108','Michał','Miszczyk',4970.15,0.00,'2025-10',0.00,'2025-09','$2y$10$jsk6KWNcQDlwQhu865FhGuHzQqZAS4l3mj0SGTwbAgq.OeTaqTayi','kierowca','firma','2025-09-04 07:36:02','aktywny','DKL83517','chgh7TqoT2y9X4uIfW8cBD:APA91bERTAfgBrbi6pXn7hqjDnbInGW_ZrzlXPWl4d0eX1WuRFUt0Mi_ax9Hb_UnzJpMPyGECTgD-bRTePv9q-3N3orF2FK65buB1IPOYtGbp4hCYAZeCZE'),
('T14','Krzysztof','Golonka',0.00,0.00,'2025-10',0.00,'2025-09','$2y$10$k4H6mnU3nnOy4UDolimGUuI7xiHXiw6cS71Z1pPFO9xrpaCzL.eF6','','firma','2025-09-03 14:34:32','aktywny','DW3NC94','dJwbb1IeSsOnCaiD3h6ym9:APA91bHM5B7ZmREn-KB4GuV9kSRAUgLqI_Pua8FBWM9YY6KczpcgxVm55wvQh3ttSlx07N5hMaQNikAerSOWJFaFdcifADKh87i6WR_JnYVs3Q3qSA3WghQ'),
('T21','Ivan','Semianyk',219.47,0.00,'2025-10',0.00,'2025-09','$2y$10$bgutnSj0jcYwmndqdejOkuFtb1lT.1T7fV38Z1qCld/DLbPWG/jJS','kierowca','firma','2025-09-04 13:07:01','aktywny','DW8YK67','dRrdQzZjRcKIKVYDG3q2Hs:APA91bEkO79pElzTVXuBIDZ7nY-YTLwbngW9c23ITGfLk2Suk38DMwhG6W6GA_Mp8uEomQE5YqEFRJF-3-n0XS_-SUjWuxTgDSM7bkDh-1G1i0ghnZuPv20'),
('T22','Rafał','Zieliński',690.84,0.00,'2025-10',0.00,'2025-09','$2y$10$h8g4jQrngfJwNCbPoK0pDe9dxJh8OSiAhHm35JmcYbLzS7LdbTUMO','kierowca','firma','2025-10-20 08:40:05','aktywny','DW6WF39','dqf_MXLvSCWqP4fFFW6apO:APA91bFwxEO7PNvZtaRlvYnWSu7XAwv4SUXdA7iJBrommcRbWwotHChGsO04px-ljGC9LX8WXffccXrngQqH9g7yOl-ZFiImRQpd569mLeebzWeO7xaFjlU'),
('test','test','',405.90,0.00,'2025-10',0.00,'2025-09','$2y$10$n6GmtFN0gj5S7Mm66SkLcuTrT3tYjYZd6Suk8bC2XftmM9QdCrnO6','flotowiec','firma','2025-09-08 10:15:45','aktywny','DW','d54oOO9iS8CWokb6VwbGor:APA91bElWxCVl624e8PV_fbqFU0GZtn9mkJE6glc3gfajCJEzBUAly5IYuJw359zc-sWdxwbyGNgmHxkQTcltQsfEODGyHRHT4O07M2ojnN4IIW4ibLAl4U'),
('test1','qwe','',0.00,0.00,'2025-10',0.00,'2025-09','$2y$10$2OIBRdwXnWEMkDrbg2EK5OZ3SEYCqGnGGl4O4w9gWtCK0bwlxAYJi','flotowiec','firma','2025-10-22 13:47:22','aktywny','DW',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kursy`
--

LOCK TABLES `kursy` WRITE;
/*!40000 ALTER TABLE `kursy` DISABLE KEYS */;
INSERT INTO `kursy` VALUES
(2,'T108',1.00,0.40,4654.95,'Voucher','Dyspozytornia',0,'2025-09-04 09:47:29',NULL),
(3,'T108',1.00,0.39,4655.34,'Karta','Dyspozytornia',0,'2025-09-04 09:48:38',NULL),
(4,'T108',49.00,19.60,4674.15,'Voucher','Dyspozytornia',0,'2025-09-04 11:09:22',NULL),
(5,'T108',35.00,14.00,4688.15,'Voucher','Dyspozytornia',0,'2025-09-04 11:45:58',NULL),
(6,'T108',100.00,40.00,4728.15,'Voucher','Dyspozytornia',0,'2025-09-04 12:57:24',NULL),
(7,'T1',250.00,194.00,194.00,'Karta','Postój',0,'2025-09-04 14:33:30',NULL),
(8,'T21',60.00,23.28,23.28,'Karta','Postój',0,'2025-09-04 15:13:27',NULL),
(9,'T108',130.00,-78.00,4970.15,'Gotówka','Postój',0,'2025-09-04 15:39:54',NULL),
(11,'test',100.00,38.80,38.80,'Karta','Postój',0,'2025-09-08 12:18:11',NULL),
(12,'test',100.00,38.80,77.60,'Karta','Postój',0,'2025-09-08 12:18:47',NULL),
(13,'test',100.00,38.80,116.40,'Karta','Postój',0,'2025-10-16 12:22:00','uploads/receipts/receipt_68f0c748dabd4.jpg'),
(14,'T22',93.00,37.20,690.84,'Voucher','Dyspozytornia',0,'2025-10-20 12:16:29',NULL),
(15,'test',100.00,38.80,205.20,'Karta','Postój',0,'2025-10-21 09:35:50','uploads/receipts/receipt_68f737d618c2d.jpg');
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pojazdy`
--

LOCK TABLES `pojazdy` WRITE;
/*!40000 ALTER TABLE `pojazdy` DISABLE KEYS */;
INSERT INTO `pojazdy` VALUES
(1,'DW8YK68','Toyota','Corolla',119699,'2026-03-21','2026-01-07',1,NULL,0,1,0,NULL,1,'2034-01-11','FUN','leasing','1092893036'),
(6,'DW3NC94','Toyota','Avensis',1,'2026-05-26','2026-08-26',1,'T14',0,1,1,'2026-02-26',1,'2031-06-08','FUN','pożyczka','1107748922'),
(7,'DKL83517','Mercedes-Benz','Vito Tourer',95149,'2099-12-12','2025-12-27',1,'T108',0,1,0,NULL,0,NULL,'POLCAR','leasing','0000'),
(9,'DW8YK67','Toyota','Corolla',119699,'2040-01-01','2040-01-01',1,'T21',0,1,0,NULL,1,'2040-01-01','FUN','leasing','0000'),
(10,'DW8YK66','Toyota','Corolla',1,'2099-01-01','2099-01-01',1,'T15',0,1,0,NULL,1,'2099-01-01','FUN','leasing','0000'),
(11,'DW','Toy','Cor',123,'2025-09-20','2025-09-25',1,'test',0,0,0,NULL,0,NULL,'FUN','tak','12313'),
(12,'DW6WF39','Toyota','Camry',259126,'2080-01-01','2080-01-01',1,'T22',0,1,1,'2080-10-01',0,NULL,'FUN','leasing','0000'),
(13,'DW6RX78','Renault','Master',1,'2099-01-01','2026-03-08',1,NULL,1,0,0,NULL,0,NULL,'FUN','własność','0000'),
(14,'DW1','Toy','Cor',5,'2026-02-12','2025-11-01',1,'test',0,1,1,'2025-10-17',0,NULL,'FUN','własne','123213');
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
('DYSPO1','Natalia Bułgajewska','POLCAR9','UZ','2025-09-11',0,0,0,0,NULL,0,NULL,1,1,'gotówka',1,0,1,1,0,0,0,1,0,1,0,'n.bulgajewska@wp.pl','Adam Witek 794480160','','','','',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refuels`
--

LOCK TABLES `refuels` WRITE;
/*!40000 ALTER TABLE `refuels` DISABLE KEYS */;
INSERT INTO `refuels` VALUES
(1,'T1','2025-09-04 14:34:16',25.65,25,150,'2025-09-04 12:34:16'),
(2,'T108','2025-09-04 16:31:42',47.87,272.38,95269,'2025-09-04 14:31:42');
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
(13,'DW9YF48','wym olej',258,'[\"uploads\\/service\\/serv_68b562a04f555.jpg\",\"uploads\\/service\\/serv_68b562a04f85a.jpg\",\"uploads\\/service\\/serv_68b562eeea731.jpg\"]','2025-09-01 11:08:48'),
(14,'DW9YF48','olej',500,'[\"uploads\\/service\\/serv_68b7f381a9997.jpg\"]','2025-09-03 09:51:29'),
(15,'DW','olej',50,'[\"uploads\\/service\\/serv_68f8b9c044a5c.jpg\"]','2025-10-22 13:02:24'),
(16,'DW','klocki',600,'[]','2025-10-22 15:12:05');
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
(11,'DW9YF48','75455','calka','zgłoszona','[\"uploads\\/damages\\/damage_68b5630b6bfc1.jpg\",\"uploads\\/damages\\/damage_68b5631c31649.jpg\"]','2025-09-01 11:10:35'),
(12,'DW','666','całka','czeka na naprawę','[\"uploads\\/damages\\/damage_68f8b9da4c990.jpg\"]','2025-10-22 13:02:50'),
(13,'DW','444','tafsdfs','zamknięta','[\"uploads\\/damages\\/damage_68f8d882ade3d.jpg\"]','2025-10-22 15:12:55');
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,'admin','$2y$10$QywPTAi6qFPfLJPRYhWAAOvjBLuP2lPK6DDBbhwTZADoTP2p8bejK','admin');
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
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_sessions`
--

LOCK TABLES `work_sessions` WRITE;
/*!40000 ALTER TABLE `work_sessions` DISABLE KEYS */;
INSERT INTO `work_sessions` VALUES
(9,'T108','DKL83517','2025-08-01 11:27:18',2,'2025-08-01 11:31:42',3),
(10,'T108','DKL83517','2025-08-01 11:37:43',88991,'2025-08-01 11:38:21',88991),
(11,'T108','DX23YB','2025-08-01 11:38:54',186001,'2025-08-01 11:48:39',186005),
(12,'T108','DKL83517','2025-08-01 11:56:00',89062,'2025-08-01 18:14:30',89169),
(13,'T108','DKL83517','2025-08-02 12:49:40',89175,'2025-08-02 17:52:00',89263),
(14,'T108','DKL83517','2025-08-04 07:31:51',89263,NULL,NULL),
(15,'T108','DKL83517','2025-08-05 06:52:35',89383,NULL,NULL),
(19,'T108','DKL83517','2025-08-05 15:26:22',89464,'2025-08-05 18:16:23',89494),
(21,'T108','DKL83517','2025-08-06 06:53:45',89494,NULL,NULL),
(29,'T108','DKL83517','2025-08-06 15:19:49',89561,'2025-08-06 19:45:31',89601),
(30,'T108','DKL83517','2025-08-07 07:02:01',89601,'2025-08-07 13:57:53',89713),
(43,'T108','DKL83517','2025-08-18 17:58:36',91935,'2025-08-19 00:36:59',92071),
(44,'T108','DKL83517','2025-08-19 07:50:18',92071,NULL,NULL),
(52,'T108','DKL83517','2025-08-20 04:45:06',92158,NULL,NULL),
(53,'T108','DKL83517','2025-08-20 13:55:11',92277,'2025-08-20 17:58:04',92329),
(57,'T108','DKL83517','2025-08-21 04:31:36',92329,NULL,NULL),
(63,'T108','DKL83517','2025-08-21 13:57:02',92405,'2025-08-21 18:03:28',92463),
(66,'T108','DKL83517','2025-08-22 04:34:30',92463,NULL,NULL),
(67,'T108','DKL83517','2025-08-22 12:58:34',92545,'2025-08-22 17:22:43',92649),
(68,'T108','DKL83517','2025-08-23 03:04:12',92649,'2025-08-23 04:32:59',92742),
(69,'T108','DKL83517','2025-08-25 07:13:37',92943,NULL,NULL),
(71,'T108','DKL83517','2025-08-25 18:14:31',93235,'2025-08-25 18:32:19',93240),
(72,'T108','DKL83517','2025-08-26 04:26:23',93240,NULL,NULL),
(74,'T108','DKL83517','2025-08-26 13:52:42',93414,'2025-08-26 18:07:31',93547),
(76,'T108','DKL83517','2025-08-27 06:57:16',93547,NULL,NULL),
(79,'T108','DKL83517','2025-08-27 14:58:43',93673,'2025-08-27 22:36:13',93790),
(82,'T108','DKL83517','2025-08-28 08:06:36',93790,NULL,NULL),
(90,'T108','DKL83517','2025-08-28 17:41:41',93970,'2025-08-28 17:49:51',93977),
(91,'T108','DKL83517','2025-08-29 07:14:59',93977,NULL,NULL),
(92,'T108','DKL83517','2025-08-29 11:27:55',94020,'2025-08-29 16:14:54',94060),
(94,'T108','DKL83517','2025-08-31 13:32:22',94178,'2025-08-31 17:56:13',94180),
(95,'T108','DKL83517','2025-09-01 04:37:24',94380,NULL,NULL),
(125,'T14','DW3NC94','2025-09-03 16:54:43',1,NULL,NULL),
(126,'T108','DKL83517','2025-09-04 09:40:40',95149,NULL,NULL),
(127,'T1','TEST','2025-09-04 14:31:45',100,'2025-09-04 14:34:25',155),
(128,'T1','TEST','2025-09-04 14:34:53',155,NULL,NULL),
(129,'T1','TEST','2025-09-04 14:36:01',155,NULL,NULL),
(130,'T21','DW8YK67','2025-09-04 15:09:36',119511,'2025-09-04 15:15:03',119600),
(131,'T21','DW8YK67','2025-09-04 15:17:04',119600,NULL,NULL),
(132,'T21','DW8YK67','2025-09-04 15:18:39',119600,NULL,NULL),
(133,'T21','DW8YK67','2025-09-04 18:54:00',119699,NULL,NULL),
(134,'T21','DW8YK68','2025-09-04 22:43:34',119699,NULL,NULL),
(135,'T21','DW8YK67','2025-09-04 22:44:16',119699,NULL,NULL),
(137,'test','DW','2025-09-08 12:17:05',1,NULL,NULL),
(138,'test','DW','2025-09-09 16:48:29',3,NULL,NULL),
(139,'test','DW','2025-10-07 16:35:43',4,'2025-10-07 16:47:10',5),
(140,'test','DW','2025-10-07 18:18:19',6,'2025-10-08 11:26:42',7),
(141,'test','DW','2025-10-08 11:27:56',8,NULL,NULL),
(142,'test','DW','2025-10-16 11:29:18',9,NULL,NULL),
(143,'test','DW','2025-10-16 11:29:59',10,'2025-10-16 11:30:25',11),
(144,'test','DW','2025-10-16 13:51:53',11,'2025-10-16 13:53:35',13),
(145,'test','DW','2025-10-16 13:53:51',14,NULL,NULL),
(146,'test','DW','2025-10-17 14:43:46',15,'2025-10-17 14:59:36',15),
(147,'test','DW','2025-10-18 19:09:36',18,'2025-10-20 10:35:11',20),
(148,'T22','DW6WF39','2025-10-20 10:45:19',259126,'2025-10-20 10:53:37',259126),
(149,'T22','DW6WF39','2025-10-20 10:54:55',259126,NULL,NULL),
(150,'test','DW','2025-10-21 09:35:22',25,NULL,NULL),
(151,'test','DW','2025-10-22 12:55:04',26,'2025-10-22 15:15:49',30),
(152,'test','DW1','2025-10-22 15:17:35',1,NULL,NULL),
(153,'test','DW1','2025-10-22 15:44:24',2,'2025-10-22 15:46:13',5),
(154,'test','DW','2025-10-22 15:46:30',31,NULL,NULL),
(155,'test1','DW','2025-10-22 15:48:14',39,NULL,NULL),
(156,'test','DW','2025-10-23 16:02:32',40,'2025-10-23 16:03:35',41),
(157,'test','DW','2025-10-24 09:33:15',123,NULL,NULL);
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

-- Dump completed on 2025-10-24 14:45:29
