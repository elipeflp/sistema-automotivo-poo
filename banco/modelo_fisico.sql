CREATE DATABASE IF NOT EXISTS estoque_veiculos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE estoque_veiculos;
CREATE TABLE IF NOT EXISTS marcas (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(80) NOT NULL, CONSTRAINT uk_marca_nome UNIQUE (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS modelos (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(80) NOT NULL, marca_id BIGINT NOT NULL,
 CONSTRAINT uk_modelo_marca_nome UNIQUE (marca_id,nome),
 CONSTRAINT fk_modelo_marca FOREIGN KEY (marca_id) REFERENCES marcas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS veiculos (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 modelo_id BIGINT NOT NULL, ano INT NOT NULL, cor VARCHAR(40) NOT NULL,
 preco DECIMAL(12,2) NOT NULL, quilometragem BIGINT NOT NULL,
 status VARCHAR(20) NOT NULL, tipo VARCHAR(20) NOT NULL,
 CONSTRAINT fk_veiculo_modelo FOREIGN KEY (modelo_id) REFERENCES modelos(id),
 CONSTRAINT ck_veiculo_ano CHECK (ano >= 1886),
 CONSTRAINT ck_veiculo_preco CHECK (preco > 0),
 CONSTRAINT ck_veiculo_km CHECK (quilometragem >= 0),
 CONSTRAINT ck_veiculo_status CHECK (status IN ('DISPONIVEL','RESERVADO','VENDIDO','DESCONTINUADO')),
 CONSTRAINT ck_veiculo_tipo CHECK (tipo IN ('CARRO','MOTO','UTILITARIO')),
 INDEX idx_veiculo_status(status), INDEX idx_veiculo_preco(preco), INDEX idx_veiculo_ano(ano)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
