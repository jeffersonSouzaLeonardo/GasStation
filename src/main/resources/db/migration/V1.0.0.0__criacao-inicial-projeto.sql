CREATE TABLE `beak` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_bomb` bigint NOT NULL,
  `id_tank` bigint NOT NULL,
  `id_fuel` bigint NOT NULL,
  `identity` varchar(100) NOT NULL,
  `hodometro` decimal(15,3) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `bomb` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_fuel` bigint NOT NULL,
  `identity` varchar(100) NOT NULL,
  `unidade_medida` varchar(10) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `empresa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `fuel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT '1',
  `id_anp` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tank` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_fuel` bigint NOT NULL,
  `capacity` decimal(10,2) NOT NULL,
  `identity` varchar(100) NOT NULL,
  `volume` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE beak ADD CONSTRAINT beak_bomb_FK FOREIGN KEY (id_bomb) REFERENCES bomb(id);
ALTER TABLE beak ADD CONSTRAINT beak_fuel_FK FOREIGN KEY (id_fuel) REFERENCES fuel(id);
ALTER TABLE beak ADD CONSTRAINT beak_tanque_FK FOREIGN KEY (id_tank) REFERENCES tank(id);
ALTER TABLE bomb ADD CONSTRAINT bomb_fuel_FK FOREIGN KEY (id_fuel) REFERENCES fuel(id);
ALTER TABLE tank ADD CONSTRAINT tank_fuel_FK FOREIGN KEY (id_fuel) REFERENCES fuel(id);

