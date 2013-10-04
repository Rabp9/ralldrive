SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `ralldrive` DEFAULT CHARACTER SET latin1 ;
USE `ralldrive` ;

-- -----------------------------------------------------
-- Table `ralldrive`.`usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ralldrive`.`usuario` (
  `idUsuario` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `usuario` VARCHAR(45) NOT NULL ,
  `clave` VARCHAR(25) NOT NULL ,
  `nombres` VARCHAR(45) NULL DEFAULT NULL ,
  `apellidoPaterno` VARCHAR(20) NULL DEFAULT NULL ,
  `apellidoMaterno` VARCHAR(20) NULL DEFAULT NULL ,
  `fechaNac` DATE NULL DEFAULT NULL ,
  `dni` CHAR(8) NULL DEFAULT NULL ,
  `correoPersonal` VARCHAR(45) NULL DEFAULT NULL ,
  `correoInstitucional` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`idUsuario`) ,
  UNIQUE INDEX `usuario_UNIQUE` (`usuario` ASC) ,
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `ralldrive`.`carpeta`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ralldrive`.`carpeta` (
  `idCarpeta` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idUsuario` INT(10) UNSIGNED NOT NULL ,
  `nombre` VARCHAR(200) NULL DEFAULT NULL ,
  `fecha` DATE NULL DEFAULT NULL ,
  `carpetaSuper` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`idCarpeta`, `idUsuario`) ,
  INDEX `fk_Carpeta_Usuario_idx` (`idUsuario` ASC) ,
  CONSTRAINT `fk_Carpeta_Usuario`
    FOREIGN KEY (`idUsuario` )
    REFERENCES `ralldrive`.`usuario` (`idUsuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `ralldrive`.`archivo`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `ralldrive`.`archivo` (
  `idArchivo` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idCarpeta` INT(10) UNSIGNED NOT NULL ,
  `idUsuario` INT(10) UNSIGNED NOT NULL ,
  `fecha` DATETIME NULL DEFAULT NULL ,
  `nombreArchivo` VARCHAR(200) NULL DEFAULT NULL ,
  `permiso` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`idArchivo`, `idCarpeta`, `idUsuario`) ,
  INDEX `fk_Archivo_Carpeta1_idx` (`idCarpeta` ASC, `idUsuario` ASC) ,
  CONSTRAINT `fk_Archivo_Carpeta1`
    FOREIGN KEY (`idCarpeta` , `idUsuario` )
    REFERENCES `ralldrive`.`carpeta` (`idCarpeta` , `idUsuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = latin1;

USE `ralldrive` ;

-- -----------------------------------------------------
-- procedure SP_LISTAR
-- -----------------------------------------------------

DELIMITER $$
USE `ralldrive`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_LISTAR`(in input_carpeta int)
BEGIN
	DROP TABLE IF EXISTS `ralldrive`.`VW_RECURSOS` ;
	CREATE TEMPORARY TABLE IF NOT EXISTS `VW_RECURSOS`
	(
		id int,
		nombre varchar(200),
		fecha date,
		tipo int
	);
	INSERT INTO VW_RECURSOS(id, nombre, fecha, tipo) SELECT c.idCarpeta, c.nombre, c.fecha, 0 FROM Carpeta c WHERE carpetaSuper = input_carpeta;
	INSERT INTO VW_RECURSOS(id, nombre, fecha, tipo) SELECT a.idArchivo, a.nombreArchivo, a.fecha, 1 FROM Archivo a WHERE idCarpeta = input_carpeta;
	SELECT id, nombre, fecha,
	CASE tipo
		WHEN 0 THEN 'Carpeta'
		WHEN 1 THEN 'Archivo'
	END as tipo
	FROM `VW_RECURSOS`;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure SP_LISTAR_ROOT
-- -----------------------------------------------------

DELIMITER $$
USE `ralldrive`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_LISTAR_ROOT`(in input_usuario int)
BEGIN
	DROP TABLE IF EXISTS `ralldrive`.`VW_RECURSOS` ;
	CREATE TEMPORARY TABLE IF NOT EXISTS `VW_RECURSOS`
	(
		id int,
		nombre varchar(200),
		fecha date,
		tipo int
	);
	INSERT INTO VW_RECURSOS(id, nombre, fecha, tipo) SELECT c.idCarpeta, c.nombre, c.fecha, 0 FROM Carpeta c WHERE idUsuario = input_usuario AND c.carpetaSuper is NULL;
	INSERT INTO VW_RECURSOS(id, nombre, fecha, tipo) SELECT a.idArchivo, a.nombreArchivo, a.fecha, 1 FROM Archivo a WHERE a.idCarpeta is NULL;
	SELECT id, nombre, fecha,
	CASE tipo
		WHEN 0 THEN 'Carpeta'
		WHEN 1 THEN 'Archivo'
	END as tipo
	FROM `VW_RECURSOS`;
END$$

DELIMITER ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
