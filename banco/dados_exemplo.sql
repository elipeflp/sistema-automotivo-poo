-- Opcional. Execute após a primeira inicialização da aplicação.
-- Dados fictícios de demonstração. Reexecução não duplica veículos do exemplo.
USE estoque_veiculos;
INSERT IGNORE INTO marcas(nome) VALUES ('Toyota'),('Honda'),('Bajaj'),('Fiat');
INSERT IGNORE INTO modelos(nome,marca_id) SELECT 'Corolla',id FROM marcas WHERE nome='Toyota';
INSERT IGNORE INTO modelos(nome,marca_id) SELECT 'Civic',id FROM marcas WHERE nome='Honda';
INSERT IGNORE INTO modelos(nome,marca_id) SELECT 'Dominar 400',id FROM marcas WHERE nome='Bajaj';
INSERT IGNORE INTO modelos(nome,marca_id) SELECT 'Strada',id FROM marcas WHERE nome='Fiat';
INSERT INTO veiculos(modelo_id,ano,cor,preco,quilometragem,status,tipo)
SELECT m.id,2022,'Branco',118000,45000,'DISPONIVEL','CARRO' FROM modelos m JOIN marcas ma ON ma.id=m.marca_id WHERE m.nome='Corolla' AND ma.nome='Toyota' AND NOT EXISTS(SELECT 1 FROM veiculos v WHERE v.modelo_id=m.id);
INSERT INTO veiculos(modelo_id,ano,cor,preco,quilometragem,status,tipo)
SELECT m.id,2021,'Cinza',112000,58000,'RESERVADO','CARRO' FROM modelos m JOIN marcas ma ON ma.id=m.marca_id WHERE m.nome='Civic' AND ma.nome='Honda' AND NOT EXISTS(SELECT 1 FROM veiculos v WHERE v.modelo_id=m.id);
INSERT INTO veiculos(modelo_id,ano,cor,preco,quilometragem,status,tipo)
SELECT m.id,2025,'Preto',23500,18000,'DISPONIVEL','MOTO' FROM modelos m JOIN marcas ma ON ma.id=m.marca_id WHERE m.nome='Dominar 400' AND ma.nome='Bajaj' AND NOT EXISTS(SELECT 1 FROM veiculos v WHERE v.modelo_id=m.id);
INSERT INTO veiculos(modelo_id,ano,cor,preco,quilometragem,status,tipo)
SELECT m.id,2023,'Vermelho',89000,37000,'VENDIDO','UTILITARIO' FROM modelos m JOIN marcas ma ON ma.id=m.marca_id WHERE m.nome='Strada' AND ma.nome='Fiat' AND NOT EXISTS(SELECT 1 FROM veiculos v WHERE v.modelo_id=m.id);
