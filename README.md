Dever�o ser constru�das interfaces Web e APIs de forma a suportar as seguintes
opera��es:
*Seguran�a
    - Permitir o cadastro de usu�rios e login com autentica��o via token JWT.
*Post
    -Permitir o cadastro e consulta de posts com texto, imagens e links.
    -Upload de Imagens
    -Apenas o criador do post poder� ter permiss�o para exclu�-lo.
*Coment�rios
    - Suportar a adi��o e exclus�o de coment�rios em posts. 
    - Os posts poder�o ser vis�veis a todos os usu�rios. 
    - Apenas o criador do coment�rio poder� ter permiss�o para exclu�-lo.
*Fotos
    - Permitir a cria��o de �lbuns de fotos. 
    - As fotos dos �lbuns poder�o ser vis�veis a todos os usu�rios. 
    - Apenas o dono de um �lbum poder� exclu�-lo.

#instalar postgres
#configura��es do BD na aplica��o (application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/sblog
spring.datasource.username=postgres
spring.datasource.password=postgres

#Inializar a aplica��o
mvn spring-boot:run


#O banco ser� criado na primeira execu��o
#Os dados b�sicos ser�o inseridos na primeira execu��o
#Acessar Interface para testes dos endpoints
http://localhost:8080/swagger-ui.html
