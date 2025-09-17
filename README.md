MTG Order Service - skeleton

Contém implementação de autenticação com JWT + refresh tokens usando JPA para usuários e refresh tokens.

Como usar:
 - Ajuste application.yml (datasource, jwt.secret)
 - Rodar: mvn spring-boot:run
 - Endpoints de exemplo:
    POST /api/auth/register {"username":"u","password":"p"}
    POST /api/auth/login {"username":"u","password":"p"}
    POST /api/auth/refresh {"refreshToken":"..."}
