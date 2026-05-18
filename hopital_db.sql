-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for hopital_db
CREATE DATABASE IF NOT EXISTS `hopital_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hopital_db`;

-- Dumping structure for table hopital_db.consultations
CREATE TABLE IF NOT EXISTS `consultations` (
  `id_consultation` int NOT NULL AUTO_INCREMENT,
  `id_patient` int NOT NULL,
  `id_medecin` int NOT NULL,
  `id_rdv` int DEFAULT NULL,
  `date_consultation` date NOT NULL,
  `diagnostic` text COLLATE utf8mb4_unicode_ci,
  `traitement` text COLLATE utf8mb4_unicode_ci,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_consultation`),
  KEY `id_patient` (`id_patient`),
  KEY `id_medecin` (`id_medecin`),
  KEY `id_rdv` (`id_rdv`),
  CONSTRAINT `consultations_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patients` (`id_patient`) ON DELETE CASCADE,
  CONSTRAINT `consultations_ibfk_2` FOREIGN KEY (`id_medecin`) REFERENCES `medecins` (`id_medecin`) ON DELETE CASCADE,
  CONSTRAINT `consultations_ibfk_3` FOREIGN KEY (`id_rdv`) REFERENCES `rendez_vous` (`id_rdv`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.consultations: ~7 rows (approximately)
INSERT INTO `consultations` (`id_consultation`, `id_patient`, `id_medecin`, `id_rdv`, `date_consultation`, `diagnostic`, `traitement`, `notes`, `created_at`) VALUES
	(1, 6, 2, 6, '2026-05-13', 'Grippe saisonni├¿re avec fi├¿vre ├á 39.2┬░C', 'Parac├®tamol 1g toutes les 8h pendant 5 jours. Ibuprof├¿ne 400mg si douleurs.', 'Repos recommand├® 3 jours. Hydratation abondante. Revoir si fi├¿vre persiste.', '2026-05-14 22:15:59'),
	(2, 7, 5, 7, '2026-05-13', 'Migraine chronique ÔÇö fr├®quence 3 fois/semaine', 'Sumatriptan 50mg ├á la crise. Propranolol 40mg/j en pr├®vention.', 'Bilan IRM recommand├®. ├ëviter les d├®clencheurs (stress, luminosit├®). RDV dans 3 semaines.', '2026-05-14 22:15:59'),
	(3, 1, 1, NULL, '2026-05-07', 'Hypertension art├®rielle stade 1 ÔÇö 145/92 mmHg', 'Amlodipine 5mg/j. R├®gime hyposod├®. Activit├® physique 30min/j.', 'Contr├┤le tensionnel dans 1 mois. ECG ├á refaire dans 6 mois.', '2026-05-14 22:15:59'),
	(4, 5, 4, NULL, '2026-04-30', 'Hernie inguinale droite ÔÇö indication op├®ratoire confirm├®e', 'Chirurgie programm├®e sous anesth├®sie g├®n├®rale. Arr├¬t anticoagulants J-5.', 'Bilan pr├®-op├®ratoire prescrit : NFS, bilan coag, ECG, radio thorax.', '2026-05-14 22:15:59'),
	(12, 8, 1, NULL, '2026-05-16', 'mlkjhgfdghj', 'lmkjhgtfdrfgvbn', 'ùsqpoiuhjbnds', '2026-05-16 20:31:45'),
	(13, 1, 1, NULL, '2026-05-18', 'fc gvbn', 'wsdxfcghvb', 'srdxfcgh', '2026-05-18 18:38:38'),
	(14, 6, 7, NULL, '2026-05-18', 'gcfhvbjn', 'fgchvjbk', 'ddxfcghvj', '2026-05-18 18:59:02');

-- Dumping structure for table hopital_db.factures
CREATE TABLE IF NOT EXISTS `factures` (
  `id_facture` int NOT NULL AUTO_INCREMENT,
  `id_patient` int NOT NULL,
  `id_consultation` int DEFAULT NULL,
  `date_facture` date NOT NULL,
  `montant` decimal(10,3) NOT NULL DEFAULT '0.000',
  `statut_paiement` enum('EN_ATTENTE','PAYE','ANNULE') COLLATE utf8mb4_unicode_ci DEFAULT 'EN_ATTENTE',
  `mode_paiement` enum('ESPECES','CARTE','VIREMENT','CHEQUE') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_facture`),
  KEY `id_patient` (`id_patient`),
  KEY `id_consultation` (`id_consultation`),
  CONSTRAINT `factures_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patients` (`id_patient`) ON DELETE CASCADE,
  CONSTRAINT `factures_ibfk_2` FOREIGN KEY (`id_consultation`) REFERENCES `consultations` (`id_consultation`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.factures: ~5 rows (approximately)
INSERT INTO `factures` (`id_facture`, `id_patient`, `id_consultation`, `date_facture`, `montant`, `statut_paiement`, `mode_paiement`, `notes`, `created_at`) VALUES
	(1, 6, 1, '2026-05-13', 45.000, 'PAYE', 'ESPECES', 'Consultation + ordonnance', '2026-05-14 22:15:59'),
	(2, 7, 2, '2026-05-13', 75.000, 'PAYE', 'CARTE', 'Consultation sp├®cialis├®e neurologie', '2026-05-14 22:15:59'),
	(3, 1, 3, '2026-05-07', 55.000, 'PAYE', 'VIREMENT', 'Consultation cardiologie + ECG', '2026-05-14 22:15:59'),
	(4, 5, 4, '2026-04-30', 120.000, 'EN_ATTENTE', NULL, 'Consultation chirurgie ÔÇö pr├®-op├®ratoire', '2026-05-14 22:15:59'),
	(5, 2, NULL, '2026-05-14', 35.000, 'EN_ATTENTE', NULL, 'Consultation p├®diatrie ÔÇö ├á r├®gler', '2026-05-14 22:15:59');

-- Dumping structure for table hopital_db.medecins
CREATE TABLE IF NOT EXISTS `medecins` (
  `id_medecin` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `specialite` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_medecin`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.medecins: ~7 rows (approximately)
INSERT INTO `medecins` (`id_medecin`, `nom`, `prenom`, `specialite`, `telephone`, `email`, `created_at`, `username`) VALUES
	(1, 'Ben Ali', 'Karim', 'Cardiologie', '+216 22 111 222', 'k.benali@hopital.tn', '2026-05-14 22:15:59', 'karimbenali'),
	(2, 'Mansouri', 'Sana', 'P├®diatrie', '+216 55 333 444', 's.mansouri@hopital.tn', '2026-05-14 22:15:59', NULL),
	(3, 'Trabelsi', 'Nadia', 'Dermatologie', '+216 98 555 666', 'n.trabelsi@hopital.tn', '2026-05-14 22:15:59', NULL),
	(4, 'Chaabane', 'Mehdi', 'Chirurgie', '+216 71 777 888', 'm.chaabane@hopital.tn', '2026-05-14 22:15:59', NULL),
	(5, 'Haddad', 'Rim', 'Neurologie', '+216 52 999 000', 'r.haddad@hopital.tn', '2026-05-14 22:15:59', NULL),
	(6, 'Oueslati', 'Yassine', 'Radiologie', '+216 27 111 333', 'y.oueslati@hopital.tn', '2026-05-14 22:15:59', 'yassine'),
	(7, 'yasmine', 'hamza', 'cardio', '99999999', 'aaa@gmail.com', '2026-05-15 11:53:02', 'yasminehamza');

-- Dumping structure for table hopital_db.patients
CREATE TABLE IF NOT EXISTS `patients` (
  `id_patient` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cin` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adresse` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `groupe_sanguin` enum('A+','A-','B+','B-','AB+','AB-','O+','O-') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_patient`),
  UNIQUE KEY `cin` (`cin`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.patients: ~9 rows (approximately)
INSERT INTO `patients` (`id_patient`, `nom`, `prenom`, `cin`, `date_naissance`, `telephone`, `email`, `adresse`, `groupe_sanguin`, `created_at`) VALUES
	(1, 'Belhaj', 'Omar', '12345678', '1985-03-15', '+216 22 001 001', 'o.belhaj@gmail.com', '12 Rue de la Paix, Tunis', 'A+', '2026-05-14 22:15:59'),
	(2, 'Gharbi', 'Ines', '23456789', '1992-07-22', '+216 55 002 002', 'i.gharbi@gmail.com', '5 Avenue Habib Bourguiba, Sfax', 'B-', '2026-05-14 22:15:59'),
	(3, 'Jebali', 'Mohamed', '34567890', '1978-11-08', '+216 98 003 003', 'm.jebali@gmail.com', '33 Rue Ibn Khaldoun, Sousse', 'O+', '2026-05-14 22:15:59'),
	(4, 'Romdhane', 'Fatma', '45678901', '2001-05-30', '+216 71 004 004', 'f.romdhane@gmail.com', '8 Rue du Commerce, Monastir', 'AB+', '2026-05-14 22:15:59'),
	(5, 'Khelifi', 'Anis', '56789012', '1965-09-12', '+216 52 005 005', 'a.khelifi@gmail.com', '21 Rue Alain Savary, Tunis', 'A-', '2026-05-14 22:15:59'),
	(6, 'Sassi', 'Leila', '67890123', '1990-01-25', '+216 27 006 006', 'l.sassi@gmail.com', '14 Avenue de Carthage, Tunis', 'O-', '2026-05-14 22:15:59'),
	(7, 'Mbarek', 'Sami', '78901234', '1975-06-18', '+216 22 007 007', 's.mbarek@gmail.com', '7 Rue de Marseille, Sfax', 'B+', '2026-05-14 22:15:59'),
	(8, 'Ayari', 'Rania', '89012345', '1998-12-05', '+216 55 008 008', 'r.ayari@gmail.com', '3 Rue de la Libert├®, Kairouan', 'AB-', '2026-05-14 22:15:59'),
	(9, 'yasmine', 'hamza', '09648344', '2003-07-11', '99999999', 'jgquy@gmail.com', 'aaaaa', NULL, '2026-05-16 13:57:20');

-- Dumping structure for table hopital_db.rendez_vous
CREATE TABLE IF NOT EXISTS `rendez_vous` (
  `id_rdv` int NOT NULL AUTO_INCREMENT,
  `id_patient` int NOT NULL,
  `id_medecin` int NOT NULL,
  `date_rdv` date NOT NULL,
  `heure_rdv` time NOT NULL,
  `motif` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `statut` enum('PLANIFIE','CONFIRME','ANNULE') COLLATE utf8mb4_unicode_ci DEFAULT 'PLANIFIE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_rdv`),
  KEY `id_patient` (`id_patient`),
  KEY `id_medecin` (`id_medecin`),
  CONSTRAINT `rendez_vous_ibfk_1` FOREIGN KEY (`id_patient`) REFERENCES `patients` (`id_patient`) ON DELETE CASCADE,
  CONSTRAINT `rendez_vous_ibfk_2` FOREIGN KEY (`id_medecin`) REFERENCES `medecins` (`id_medecin`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.rendez_vous: ~17 rows (approximately)
INSERT INTO `rendez_vous` (`id_rdv`, `id_patient`, `id_medecin`, `date_rdv`, `heure_rdv`, `motif`, `statut`, `created_at`) VALUES
	(1, 1, 1, '2026-05-14', '09:00:00', 'Douleurs thoraciques', 'CONFIRME', '2026-05-14 22:15:59'),
	(2, 2, 2, '2026-05-14', '10:30:00', 'Consultation p├®diatrique', 'PLANIFIE', '2026-05-14 22:15:59'),
	(3, 3, 1, '2026-05-14', '11:00:00', 'Bilan cardiaque annuel', 'PLANIFIE', '2026-05-14 22:15:59'),
	(4, 4, 3, '2026-05-14', '14:00:00', 'Allergie cutan├®e', 'CONFIRME', '2026-05-14 22:15:59'),
	(5, 5, 4, '2026-05-14', '15:30:00', 'Post-op├®ratoire', 'PLANIFIE', '2026-05-14 22:15:59'),
	(6, 6, 2, '2026-05-13', '09:30:00', 'Fi├¿vre persistante', 'CONFIRME', '2026-05-14 22:15:59'),
	(7, 7, 5, '2026-05-13', '11:00:00', 'Maux de t├¬te chroniques', 'CONFIRME', '2026-05-14 22:15:59'),
	(8, 1, 3, '2026-05-13', '14:30:00', '├ëruption cutan├®e', 'ANNULE', '2026-05-14 22:15:59'),
	(9, 8, 1, '2026-05-15', '09:00:00', 'Hypertension art├®rielle', 'PLANIFIE', '2026-05-14 22:15:59'),
	(10, 3, 6, '2026-05-15', '10:00:00', 'Imagerie thoracique', 'PLANIFIE', '2026-05-14 22:15:59'),
	(11, 5, 2, '2026-05-16', '11:30:00', 'Suivi p├®diatrique', 'PLANIFIE', '2026-05-14 22:15:59'),
	(12, 1, 3, '2026-05-13', '14:30:00', '├ëruption cutan├®e', 'ANNULE', '2026-05-18 18:37:30'),
	(13, 8, 7, '2026-05-18', '08:30:00', 'grippe', 'PLANIFIE', '2026-05-18 18:55:08'),
	(14, 6, 7, '2026-05-18', '20:00:00', 'lkj', 'PLANIFIE', '2026-05-18 18:58:02'),
	(15, 8, 7, '2026-05-18', '12:30:00', 'sdfghjk', 'PLANIFIE', '2026-05-18 20:46:13'),
	(16, 9, 7, '2026-05-18', '09:30:00', 'xfcgvhn,j', 'PLANIFIE', '2026-05-18 20:54:12'),
	(17, 8, 6, '2026-05-18', '23:59:00', 'dfghjk', 'PLANIFIE', '2026-05-18 22:55:08');

-- Dumping structure for table hopital_db.utilisateurs
CREATE TABLE IF NOT EXISTS `utilisateurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','MEDECIN','SECRETAIRE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table hopital_db.utilisateurs: ~8 rows (approximately)
INSERT INTO `utilisateurs` (`id`, `username`, `password`, `role`, `created_at`) VALUES
	(1, 'admin', 'admin123', 'ADMIN', '2026-05-14 22:15:59'),
	(2, 'medecin', 'med123', 'MEDECIN', '2026-05-14 22:15:59'),
	(3, 'medecin2', 'med123', 'MEDECIN', '2026-05-14 22:15:59'),
	(4, 'secretaire', 'sec123', 'SECRETAIRE', '2026-05-14 22:15:59'),
	(5, 'medecin3', 'med123', 'MEDECIN', '2026-05-15 12:07:57'),
	(8, 'yasminehamza', 'med123', 'MEDECIN', '2026-05-18 20:53:16'),
	(9, 'karimbenali', 'med123', 'MEDECIN', '2026-05-18 20:56:30'),
	(10, 'yassine', 'med123', 'MEDECIN', '2026-05-18 22:53:43');

-- Dumping structure for view hopital_db.vue_consultations
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vue_consultations` (
	`id_consultation` INT(10) NOT NULL,
	`date_consultation` DATE NOT NULL,
	`diagnostic` TEXT NULL COLLATE 'utf8mb4_unicode_ci',
	`traitement` TEXT NULL COLLATE 'utf8mb4_unicode_ci',
	`notes` TEXT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_nom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_prenom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_cin` VARCHAR(20) NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_nom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_prenom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_specialite` VARCHAR(100) NULL COLLATE 'utf8mb4_unicode_ci'
) ENGINE=MyISAM;

-- Dumping structure for view hopital_db.vue_factures
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vue_factures` (
	`id_facture` INT(10) NOT NULL,
	`date_facture` DATE NOT NULL,
	`montant` DECIMAL(10,3) NOT NULL,
	`statut_paiement` ENUM('EN_ATTENTE','PAYE','ANNULE') NULL COLLATE 'utf8mb4_unicode_ci',
	`mode_paiement` ENUM('ESPECES','CARTE','VIREMENT','CHEQUE') NULL COLLATE 'utf8mb4_unicode_ci',
	`notes` VARCHAR(255) NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_nom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_prenom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_cin` VARCHAR(20) NULL COLLATE 'utf8mb4_unicode_ci'
) ENGINE=MyISAM;

-- Dumping structure for view hopital_db.vue_rendez_vous
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vue_rendez_vous` (
	`id_rdv` INT(10) NOT NULL,
	`date_rdv` DATE NOT NULL,
	`heure_rdv` TIME NOT NULL,
	`motif` VARCHAR(255) NULL COLLATE 'utf8mb4_unicode_ci',
	`statut` ENUM('PLANIFIE','CONFIRME','ANNULE') NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_nom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_prenom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`patient_tel` VARCHAR(20) NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_nom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_prenom` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`medecin_specialite` VARCHAR(100) NULL COLLATE 'utf8mb4_unicode_ci'
) ENGINE=MyISAM;

-- Dumping structure for view hopital_db.vue_consultations
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vue_consultations`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vue_consultations` AS select `c`.`id_consultation` AS `id_consultation`,`c`.`date_consultation` AS `date_consultation`,`c`.`diagnostic` AS `diagnostic`,`c`.`traitement` AS `traitement`,`c`.`notes` AS `notes`,`p`.`nom` AS `patient_nom`,`p`.`prenom` AS `patient_prenom`,`p`.`cin` AS `patient_cin`,`m`.`nom` AS `medecin_nom`,`m`.`prenom` AS `medecin_prenom`,`m`.`specialite` AS `medecin_specialite` from ((`consultations` `c` join `patients` `p` on((`c`.`id_patient` = `p`.`id_patient`))) join `medecins` `m` on((`c`.`id_medecin` = `m`.`id_medecin`)));

-- Dumping structure for view hopital_db.vue_factures
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vue_factures`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vue_factures` AS select `f`.`id_facture` AS `id_facture`,`f`.`date_facture` AS `date_facture`,`f`.`montant` AS `montant`,`f`.`statut_paiement` AS `statut_paiement`,`f`.`mode_paiement` AS `mode_paiement`,`f`.`notes` AS `notes`,`p`.`nom` AS `patient_nom`,`p`.`prenom` AS `patient_prenom`,`p`.`cin` AS `patient_cin` from (`factures` `f` join `patients` `p` on((`f`.`id_patient` = `p`.`id_patient`)));

-- Dumping structure for view hopital_db.vue_rendez_vous
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vue_rendez_vous`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vue_rendez_vous` AS select `r`.`id_rdv` AS `id_rdv`,`r`.`date_rdv` AS `date_rdv`,`r`.`heure_rdv` AS `heure_rdv`,`r`.`motif` AS `motif`,`r`.`statut` AS `statut`,`p`.`nom` AS `patient_nom`,`p`.`prenom` AS `patient_prenom`,`p`.`telephone` AS `patient_tel`,`m`.`nom` AS `medecin_nom`,`m`.`prenom` AS `medecin_prenom`,`m`.`specialite` AS `medecin_specialite` from ((`rendez_vous` `r` join `patients` `p` on((`r`.`id_patient` = `p`.`id_patient`))) join `medecins` `m` on((`r`.`id_medecin` = `m`.`id_medecin`)));

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
