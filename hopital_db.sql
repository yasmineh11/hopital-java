-- Dumping database structure for hopital_db
CREATE DATABASE IF NOT EXISTS `hopital_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hopital_db`;

-- Dumping structure for table hopital_db.medecins
CREATE TABLE IF NOT EXISTS `medecins` (
  `id_medecin` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `specialite` varchar(100) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_medecin`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table hopital_db.medecins: ~0 rows (approximately)

-- Dumping structure for table hopital_db.utilisateurs
CREATE TABLE IF NOT EXISTS `utilisateurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','MEDECIN','SECRETAIRE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table hopital_db.utilisateurs: ~1 rows (approximately)
INSERT INTO `utilisateurs` (`id`, `username`, `password`, `role`) VALUES
	(1, 'admin', 'admin123', 'ADMIN');
	

-- Table patients
CREATE TABLE IF NOT EXISTS patients (
  id_patient     INT AUTO_INCREMENT PRIMARY KEY,
  nom            VARCHAR(100) NOT NULL,
  prenom         VARCHAR(100) NOT NULL,
  cin            VARCHAR(20)  UNIQUE,
  date_naissance DATE,
  telephone      VARCHAR(20),
  email          VARCHAR(150),
  adresse        VARCHAR(255)
);

-- Table rendez_vous
CREATE TABLE IF NOT EXISTS rendez_vous (
  id_rdv         INT AUTO_INCREMENT PRIMARY KEY,
  id_patient     INT NOT NULL,
  id_medecin     INT NOT NULL,
  date_rdv       DATE NOT NULL,
  heure_rdv      TIME NOT NULL,
  motif          VARCHAR(255),
  statut         ENUM('PLANIFIE','CONFIRME','ANNULE') DEFAULT 'PLANIFIE',
  FOREIGN KEY (id_patient) REFERENCES patients(id_patient),
  FOREIGN KEY (id_medecin) REFERENCES medecins(id_medecin)
);

-- Créer le compte secrétaire par défaut
INSERT INTO utilisateurs (username, password, role)
VALUES ('secretaire', 'sec123', 'SECRETAIRE')
ON DUPLICATE KEY UPDATE username=username;
