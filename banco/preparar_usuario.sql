-- Execute como administrador do MySQL, uma vez, para execução local.
CREATE DATABASE IF NOT EXISTS estoque_veiculos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'estoque'@'localhost' IDENTIFIED BY 'estoque_local_2026';
GRANT ALL PRIVILEGES ON estoque_veiculos.* TO 'estoque'@'localhost';
