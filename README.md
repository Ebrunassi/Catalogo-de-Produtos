# **Java - Catálogo de Produtos**
O serviço foi desenvolvido utilizando *Java 11*, e nele consta as implementações dos seguintes requests: <br />

Verbo HTTP | Resource Path | Descrição
----|------|--------
POST| http://localhost:9999/products | Criação de um produto
PUT | http://localhost:9999/products/{id} | Atualização de um produto
GET | http://localhost:9999/products/{id} | Busca um prouto por ID
GET | http://localhost:9999/products | Lista de todos os produtos
GET | http://localhost:9999/products/search | Lista de produtos filtrados
DELETE | http://localhost:9999/products/{id} | Deleção de um produto

## **Base de dados**
Nesta aplicação foi utilizado o banco **H2 no modo integrado**. Dessa forma, os dados persistidos na base terão uma vida útil até o momento em que o serviço for encerrado.<br />
É possível visualizar o banco através de um console, acessando a url : <br />
http://localhost:9999/h2-console
<br />

Onde a autenticação de acesso deve ser feita utilizando os seguintes valores:
Campo de configuração | Valor a ser atribuido
----|------
Save Settings| Generic H2 (Embedded)
Setting name | Generic H2 (Embedded)
Driver Class | org.h2.Driver
JDBC URL | jdbc:h2:mem:compassodb
User Name | compasso
Password | 123

## **Swagger**
É possível visualizar mais informações a respeito dos endpoints deste serviço e até mesmo testá-los através do swagger implementado. <br />
Uma vez que o projeto esteja em execução, é possível acessar o swagger através do link: <br />
http://localhost:9999/swagger-ui.html#/

## **Logs**
O serviço contém logs e estes são armazenados na pasta ***logs*** dentro do projeto. Os logs são compactados e salvos de acordo com o dia em que foram capturados. <br />
Para a visualização dos logs mais recentes, basta acessar o arquivo ***logs/catalogo-produtos.log***. <br />


## **Testes Unitários**
Para este serviço foi implementado testes unitários utilizando **JUnit**. <br />
Os testes podem ser encontrados e executados no diretório *src/test/java*.

