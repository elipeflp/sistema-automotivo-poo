# Sistema Automotivo - Gestão de Estoque de Veículos

**Autor:** Felipe Ferreira Freire  
**Disciplina:** Object Oriented Programming  
**Tecnologias:** Java 17, Spring Boot 3.5.7, Spring Data JPA, MySQL, HTML/CSS/JavaScript.

Aplicação acadêmica com cadastro, consulta, filtros, atualização e exclusão de veículos. Inclui CRUD de marcas e modelos, validação e interface web responsiva. O banco da aplicação é MySQL. H2 é usado somente nos testes automatizados.

Os relatórios em PDF, os prints e o executável pronto acompanham o pacote ZIP. Este repositório reúne o código-fonte, os scripts SQL e os resultados dos testes.

## 1. Executar no Windows sem compilar

1. Extraia o pacote ZIP em uma pasta. O JAR pronto está no ZIP; ao baixar pelo GitHub, compile com `mvn package` e use o JAR de `target/`.
2. Instale Java 17 ou superior e confirme no PowerShell: `java -version`.
3. Prepare o MySQL por **uma** das alternativas abaixo.
4. Na pasta `sistema-automotivo`, execute `iniciar.bat` ou rode `java -jar dist/sistema-automotivo-1.0.0.jar` no PowerShell.
5. Aguarde a mensagem `Started EstoqueApplication` e abra **http://localhost:8080** no navegador.
6. Mantenha o terminal aberto enquanto usa o sistema. Para encerrar: Ctrl+C.

### Alternativa A: MySQL pelo Docker Desktop

Com Docker Desktop iniciado, abra o PowerShell na pasta do projeto:

```powershell
docker compose up -d db
docker compose ps
```

Aguarde o banco ficar `healthy` antes de iniciar a aplicação. A configuração do arquivo `compose.yaml` já coincide com a aplicação. Para parar o banco sem apagar os dados: `docker compose stop`.

### Alternativa B: MySQL já instalado

Use MySQL 8.0.16 ou superior (8.0/8.4). No MySQL Workbench, conecte com uma conta administradora e execute `banco/preparar_usuario.sql`. O serviço deve estar disponível em `localhost:3306`.

Se preferir uma conta própria, configure no mesmo PowerShell antes de iniciar:

```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
$env:DB_URL="jdbc:mysql://localhost:3306/estoque_veiculos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo"
java -jar dist/sistema-automotivo-1.0.0.jar
```

As tabelas são criadas pelo script SQL embarcado; o Hibernate valida o mapeamento na inicialização. O banco deve existir ou o usuário deve poder criá-lo. Credenciais padrão de demonstração local: usuário `estoque`, senha `estoque_local_2026`.

## 2. Usar o sistema

1. Em **Marcas e modelos**, cadastre uma marca e um modelo associado.
2. Em **Estoque de veículos**, clique em **Cadastrar veículo**.
3. Preencha modelo, ano, cor, preço, quilometragem, status e tipo.
4. Combine os filtros desejados e clique em **Aplicar filtros**.
5. Use **Editar** para alterar dados de um veículo.
6. Para excluir, primeiro altere o status para **Vendido** ou **Descontinuado**. Depois clique em **Excluir** e confirme.

A marca do veículo é obtida de seu modelo, evitando associações contraditórias. Não é possível excluir marcas com modelos nem modelos com veículos vinculados. Os indicadores no topo refletem somente a consulta atual.

Para carregar os quatro exemplos dos prints, execute `banco/dados_exemplo.sql` no MySQL Workbench depois da primeira inicialização. Os dados são fictícios. O sistema começa vazio por padrão.

## 3. Compilar e testar

Requer JDK 17 e Maven 3.6.3 ou superior. Na pasta com `pom.xml`:

```sh
mvn test
mvn package
java -jar target/sistema-automotivo-1.0.0.jar
```

`mvn test` usa H2 em memória, não depende do MySQL e reverte os dados de cada teste. Os 12 testes verificam CRUD, filtros, validação, vínculos e regras de exclusão. A extensão Mockito usa o modo subclass, dispensando anexação de agente à JVM.

Para atualizar o executável de distribuição depois de alterar o código, copie `target/sistema-automotivo-1.0.0.jar` para `dist/`.

## 4. Organização

| Caminho | Conteúdo |
|---|---|
| `src/main/java/.../domain` | Entidades, herança e enums |
| `src/main/java/.../api` | Controllers REST, DTOs e tratamento de erros |
| `src/main/java/.../service` | Regras de negócio e transações |
| `src/main/java/.../repository` | Persistência Spring Data JPA |
| `src/main/resources/static` | Interface HTML/CSS/JavaScript |
| `src/main/resources/schema-mysql.sql` | Modelo físico executado na inicialização |
| `src/test` | Testes automatizados e configuração isolada |
| `banco` | Scripts SQL para MySQL |
| `dist` (no pacote ZIP) | Executável Java com dependências |
| `evidencias` | Prints reais e resultados da validação |

## 5. API REST

| Método | Rota | Operação |
|---|---|---|
| GET / POST | `/api/marcas` | Listar / cadastrar marcas |
| PUT / DELETE | `/api/marcas/{id}` | Atualizar / excluir marca |
| GET / POST | `/api/modelos` | Listar / cadastrar modelos |
| PUT / DELETE | `/api/modelos/{id}` | Atualizar / excluir modelo |
| GET / POST | `/api/veiculos` | Consultar / cadastrar veículos |
| GET / PUT / DELETE | `/api/veiculos/{id}` | Obter / atualizar / excluir veículo |

Consulta de modelos aceita `marcaId`. Consulta de veículos aceita `marcaId`, `modeloId`, `precoMin`, `precoMax`, `anoMin`, `anoMax`, `status` e `tipo`. Para um ano exato, use o mesmo valor em `anoMin` e `anoMax`. Intervalos são inclusivos.

Exemplo de corpo de POST/PUT `/api/veiculos` (substitua `modeloId` por um ID existente):

```json
{
  "modeloId": 1,
  "ano": 2024,
  "cor": "Preto",
  "preco": 120000.00,
  "quilometragem": 20000,
  "status": "DISPONIVEL",
  "tipo": "CARRO"
}
```

Status: `DISPONIVEL`, `RESERVADO`, `VENDIDO`, `DESCONTINUADO`. Tipos: `CARRO`, `MOTO`, `UTILITARIO`. PUT recebe todos os campos obrigatórios. Respostas: 200 para leitura/edição, 201 para criação, 204 para exclusão, 400 para dados inválidos, 404 para ID inexistente e 409 para conflitos de regra/vínculo.

## 6. Código-fonte

Repositório público: https://github.com/elipeflp/sistema-automotivo-poo

## 7. Validação realizada

Consulte `evidencias/resultado-testes.txt`, `testes-mysql.json` e `testes-interface.json`. Os prints foram obtidos da aplicação Spring Boot executando com MySQL 8.0.46. O teste verificou persistência após reinício da aplicação. As medições são locais, com quatro veículos; não são teste de carga.

A aplicação é um protótipo acadêmico local, sem autenticação, histórico de vendas ou paginação. Em volumes maiores, paginação e consulta de modelos diretamente no repositório podem ser avaliadas em outra versão. Índices de preço, ano e status apoiam a consulta, mas não substituem medição de desempenho com uma base representativa.

## 8. Problemas comuns

- **Access denied:** confira usuário/senha, execute o script de preparação como administrador e use o mesmo terminal para definir as variáveis e iniciar.
- **Communications link failure:** confirme que o MySQL está iniciado e na porta 3306.
- **Porta 8080 ocupada:** no PowerShell, defina `$env:PORT="8081"` e abra http://localhost:8081.
- **Porta 3306 ocupada com Docker:** use o MySQL existente ou ajuste a porta do Compose e a variável DB_URL.
- **Java não reconhecido:** instale o JDK e reabra o terminal para atualizar PATH.
- **Alterações no código não aparecem:** recompile e atualize o JAR de `dist/` ou execute diretamente o JAR de `target/`.

## Referências técnicas

- https://spring.io/guides/gs/rest-service/
- https://spring.io/guides/gs/accessing-data-jpa/
- https://docs.spring.io/spring-boot/3.5/reference/data/sql.html
- https://dev.mysql.com/doc/refman/8.4/en/create-table.html
