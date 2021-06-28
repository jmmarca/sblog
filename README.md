Deverão ser construídas interfaces Web e APIs de forma a suportar as seguintes
operações:
-Segurança
    - Permitir o cadastro de usuários e login com autenticação via token JWT.
*Post
    -Permitir o cadastro e consulta de posts com texto, imagens e links.
    -Upload de Imagens
	-Apenas o criador do post poderá ter permissão para excluí-lo.
*Comentários
	- Suportar a adição e exclusão de comentários em posts. 
	- Os posts poderão ser visíveis a todos os usuários. 
	- Apenas o criador do comentário poderá ter permissão para excluí-lo.
*Fotos
	- Permitir a criação de álbuns de fotos. 
	- As fotos dos álbuns poderão ser visíveis a todos os usuários. 
	- Apenas o dono de um álbum poderá excluí-lo.


#instalar postgres
#configurações do BD na aplicação (application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/sblog
spring.datasource.username=postgres
spring.datasource.password=postgres


#Inializar a aplicação
mvn spring-boot:run


#O banco será criado na primeira execução
#Os dados básicos serão inseridos na primeira execução
#Acessar Interface para testes dos endpoints
http://localhost:8080/swagger-ui.html
