create database bd_sistema_inventario;

use bd_sistema_inventario;
 -- crear la base de datos para usuarios 
 
 create table tb_usuarios(
 IdUsuario int(15) auto_increment primary key,
 nombre varchar(30) not null,
 apellido varchar(30) not null,
 usuario varchar (30) not null,
 password varchar(15) not null,
 telefono varchar(10) not null,
 estado int (1) not null
 
 
 ); 
 INSERT INTO tb_usuarios(nombre, apellido, usuario, password, telefono, estado)
VALUES ("Karol", "Pinto", "Karol03", "1234", "3228529996", 1);
select * from tb_usuarios;
SHOW TABLES LIKE 'usuarios';

select usuario, password from tb_usuarios where usuario ="Karol03" and password = "1234";
 
 
 
 
 create table tb_proveedores(
 IdProveedor int(15) auto_increment primary key,
 nombre varchar(30) not null,
 apellido varchar(30) not null,
 cedula varchar(15) not null,
 telefono varchar(10) not null,
 empresa_proveedora varchar(30) not null,
 estado int (1) not null
 
 );
  create table tb_productos(
 IdProducto int(15) auto_increment primary key,
 nombre varchar(100) not null,
 cantidad int(11) not null,
 precio double(10,2) not null,
 descripcion varchar(200) not null,
 estado int (1) not null
 );
   create table tb_facturas(
 IdProducto int(15) auto_increment primary key,
 cantidad int(11) not null,
 precio double(10,2) not null,
 total_a_Pagar double(10,2) not null,
 estado int (1) not null
 );
 
 
 show tables; 
 
 select *from tb_facturas;
 