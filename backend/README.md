# Sistema de Gerenciamento de Artistas e Álbuns

## 📋 Descrição do Projeto

Sistema API REST desenvolvido em Spring Boot para gerenciamento de artistas, álbuns e imagens, com autenticação JWT, upload de arquivos para MinIO, notificações em tempo real via WebSocket e sincronização de dados externos.

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Docker e Docker Compose instalados
- Git instalado

### Passos para Execução

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd backend

# 2. Execute com Docker Compose
docker-compose up --build

# 3. Acesse a aplicação
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# MinIO Console: http://localhost:9001
# Banco PostgreSQL: localhost:5432
```

A aplicação será executada automaticamente com:
- ✅ Banco PostgreSQL configurado
- ✅ Migrações Flyway aplicadas
- ✅ MinIO configurado para armazenamento de imagens
- ✅ API REST disponível na porta 8080
- ✅ WebSocket disponível para notificações

### Usuário Inicial
```
Admin: username=admin, password=admin123
```

## 🏗️ Arquitetura do Projeto

### Estrutura de Pastas

```
src/main/java/com/anthony/backend/
├── controller/            # Endpoints REST (API Layer)
│   ├── AlbumController.java
│   ├── AlbumImageController.java
│   ├── ArtistController.java
│   ├── AuthController.java
│   ├── UserController.java
│   └── dto/               # DTOs de request/response
├── application/           # Camada de aplicação
│   ├── service/           # Regras de negócio (Business Layer)
│   └── mapper/            # Conversão entre entidades e DTOs (MapStruct)
├── domain/                # Camada de domínio
│   ├── model/             # Entidades do domínio (Models)
│   │   ├── Album.java
│   │   ├── Artist.java
│   │   ├── AlbumImage.java
│   │   └── User.java
│   ├── repository/        # Contratos de repositórios
│   └── exception/         # Exceções personalizadas
├── infrastructure/        # Camada de infraestrutura
│   ├── config/            # Configurações gerais
│   ├── persistence/       # Implementação de repositórios (JPA)
│   ├── security/          # Configurações de segurança JWT
│   ├── storage/           # Integração com MinIO
│   ├── websocket/         # Configuração WebSocket
│   ├── ratelimit/         # Rate limiting (Bucket4j)
│   └── exception/         # Tratamento global de exceções
└── regionais/             # Módulo de sincronização de regionais
    ├── controller/        # Endpoints de sincronização
    ├── service/           # Lógica de sincronização
    ├── domain/            # Entidade Regional
    ├── repository/        # Repositório de regionais
    ├── dto/               # DTOs para API externa
    ├── client/            # Cliente HTTP (RestTemplate)
    └── config/            # Configurações do módulo

src/main/resources/
├── db/migration/          # Scripts Flyway (versionamento do banco)
│   ├── V1__create_artist_table.sql
│   ├── V2__create_album_table.sql
│   ├── V3__create_album_image_table.sql
│   ├── V4__insert_initial_data.sql
│   ├── V5__add_is_default_to_album_image.sql
│   ├── V6__rename_file_name_to_file_key_and_drop_file_url.sql
│   ├── V7__create_user_table.sql
│   └── V8__create_regionais_table.sql
├── application.properties # Configurações da aplicação
└── static/                # Recursos estáticos
```

### Princípios Arquiteturais

A aplicação segue uma **arquitetura em camadas limpa** (Clean Architecture) adaptada para Spring Boot, garantindo:
- **Separação de responsabilidades**: Cada camada tem uma função bem definida
- **Baixo acoplamento**: As camadas dependem de abstrações, não de implementações
- **Alta coesão**: Componentes relacionados estão agrupados
- **Testabilidade**: Facilita a criação de testes unitários e de integração
- **Manutenibilidade**: Mudanças em uma camada não impactam diretamente outras

#### Camadas da Aplicação:

- **Controller (API Layer)**: Expõe os endpoints REST, valida entrada e formata saídas
- **Application (Business Layer)**: Contém services com regras de negócio e mappers para conversão de dados
- **Domain (Core Layer)**: Define as entidades do sistema e contratos (interfaces) - núcleo da aplicação
- **Infrastructure (Technical Layer)**: Implementa detalhes técnicos como persistência, segurança, integrações externas e configurações

Essa estrutura promove:
- Reutilização de código através de services e mappers
- Isolamento de regras de negócio no domínio
- Flexibilidade para trocar implementações de infraestrutura
- Facilita evoluções e novas funcionalidades sem impactar o core

## 🔧 Tecnologias e Recursos

### Stack Principal
- **Spring Boot 4.0.1** - Framework principal
- **Java 17** - Linguagem de programação
- **PostgreSQL** - Banco de dados relacional
- **Flyway** - Controle de versão do banco de dados
- **Docker** - Containerização da aplicação

### Bibliotecas e Frameworks

#### 🔐 **Autenticação e Autorização**
- **Spring Security** - Framework de segurança
- **JWT (JSON Web Token)** - Tokens de autenticação
- **jjwt 0.12.3** - Biblioteca para manipulação de JWT
- **Refresh Tokens** - Renovação de tokens sem novo login
- **Roles**: USER e ADMIN

#### 📦 **Persistência e Banco de Dados**
- **Spring Data JPA** - Abstração de persistência
- **Hibernate** - ORM (Object-Relational Mapping)
- **Flyway** - Migrações de banco de dados
- **PostgreSQL Driver** - Conectividade com PostgreSQL

#### 🎨 **Mapeamento e Validação**
- **MapStruct 1.5.5** - Mapeamento de objetos (Entity ↔ DTO)
- **Bean Validation** - Validações declarativas
- **Lombok** - Redução de boilerplate

#### 📡 **APIs e Comunicações**
- **Spring Web MVC** - Framework web REST
- **Spring WebSocket** - Comunicação bidirecional em tempo real
- **RestTemplate** - Cliente HTTP para APIs externas
- **STOMP** - Protocolo de mensagens sobre WebSocket

#### 📄 **Documentação**
- **SpringDoc OpenAPI 2.3.0** - Geração automática de documentação
- **Swagger UI** - Interface interativa para testar endpoints

#### 💾 **Armazenamento e Cache**
- **MinIO 8.5.7** - Armazenamento de objetos (imagens)
- **Bucket4j 8.7.0** - Rate limiting em memória

#### ⚡ **Monitoramento e Observabilidade**
- **Spring Actuator** - Endpoints de health check e métricas
- **Logs estruturados** - Logging com padrões de produção

### Recursos Implementados

#### 🔐 **Autenticação e Segurança**
- Login com username/password
- Geração de access token (JWT) e refresh token
- Endpoints protegidos por autenticação
- Controle de acesso baseado em roles (RBAC)
- Expiração automática de tokens
- CORS configurado

#### 🎵 **Funcionalidades de Negócio**
- **Artistas**: CRUD completo com paginação e filtros
- **Álbuns**: Criação, listagem, atualização e exclusão
- **Imagens de Álbuns**: Upload, download, definir padrão e exclusão
- **Usuários**: Gerenciamento de usuários (ADMIN)
- **Sincronização de Regionais**: Integração com API externa para sincronizar dados

#### 📊 **Integrações Externas**
- **MinIO**: Upload e armazenamento de imagens
- **API de Regionais**: Sincronização de dados da Polícia Civil
- **RestTemplate**: Cliente HTTP para consumo de APIs

#### 🔔 **Notificações em Tempo Real**
- WebSocket configurado com STOMP
- Notificações automáticas ao criar álbuns
- Canal `/topic/notifications` para broadcast

#### ⚡ **Performance e Controle**
- Rate Limiting com Bucket4j (proteção contra abuso)
- Paginação em listagens
- Filtros de busca otimizados
- Lazy loading de relacionamentos JPA

#### 🛡️ **Qualidade e Confiabilidade**
- Validações Bean Validation em todas as entradas
- Tratamento global de exceções
- Transações ACID
- Soft delete (quando aplicável)
- Health checks via Actuator

#### 🎯 **Boas Práticas**
- Padrão DTO para transferência de dados
- MapStruct para conversões automáticas
- Repository Pattern para persistência
- Service Layer para regras de negócio
- Exception Handler centralizado
- Logs estruturados

## 📡 Endpoints da API

### 🔐 Autenticação (`/api/auth`)
```
POST   /api/auth/register     - Registrar novo usuário
POST   /api/auth/login        - Login (retorna access e refresh tokens)
POST   /api/auth/refresh      - Renovar access token usando refresh token
```

### 👥 Usuários (`/api/users`) - Requer autenticação ADMIN
```
GET    /api/users             - Listar todos os usuários
GET    /api/users/{id}        - Buscar usuário por ID
```

### 🎵 Artistas (`/api/v1/artist`) - Requer autenticação
```
POST   /api/v1/artist         - Criar artista
GET    /api/v1/artist/{id}    - Buscar artista por ID
GET    /api/v1/artist         - Listar artistas (com paginação e filtros)
PUT    /api/v1/artist/{id}    - Atualizar artista
DELETE /api/v1/artist/{id}    - Excluir artista
```

### 💿 Álbuns (`/api/v1/album`) - Requer autenticação
```
POST   /api/v1/album          - Criar álbum (associado a um artista)
GET    /api/v1/album/{id}     - Buscar álbum por ID
GET    /api/v1/album          - Listar álbuns (com paginação e filtros)
PUT    /api/v1/album/{id}     - Atualizar álbum
DELETE /api/v1/album/{id}     - Excluir álbum
```

### 🖼️ Imagens de Álbuns (`/api/v1/album/{albumId}/images`) - Requer autenticação
```
POST   /api/v1/album/{albumId}/images              - Upload de imagem (multipart/form-data)
GET    /api/v1/album/{albumId}/images              - Listar todas as imagens do álbum
GET    /api/v1/album/{albumId}/images/{imageId}    - Baixar imagem específica
PATCH  /api/v1/album/{albumId}/images/{imageId}/default - Definir como imagem padrão
DELETE /api/v1/album/{albumId}/images/{imageId}    - Excluir imagem
```

### 🔄 Sincronização (`/api/sync`) - Público
```
POST   /api/sync/regionais    - Sincronizar regionais da API externa
```

### ✅ Health Check (`/actuator`)
```
GET    /actuator/health       - Status de saúde da aplicação
GET    /actuator/info         - Informações da aplicação
```

### 📚 Documentação (`/swagger-ui.html`)
```
GET    /swagger-ui.html       - Interface Swagger UI para testar APIs
GET    /v3/api-docs           - Documentação OpenAPI 3.0 em JSON
```

## 🔧 Exemplos de Uso

### 1. Registrar e Fazer Login
```bash
# Registrar novo usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "anthony",
    "password": "senha123",
    "role": "USER"
  }'

# Fazer login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "anthony",
    "password": "senha123"
  }'

# Resposta:
# {
#   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "username": "anthony",
#   "role": "USER"
# }
```

### 2. Criar Artista
```bash
curl -X POST http://localhost:8080/api/v1/artist \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu-access-token}" \
  -d '{
    "name": "Pink Floyd"
  }'
```

### 3. Criar Álbum
```bash
curl -X POST http://localhost:8080/api/v1/album \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu-access-token}" \
  -d '{
    "title": "The Dark Side of the Moon",
    "artistId": 1
  }'
```

### 4. Upload de Imagem do Álbum
```bash
curl -X POST http://localhost:8080/api/v1/album/1/images \
  -H "Authorization: Bearer {seu-access-token}" \
  -F "file=@/caminho/para/imagem.jpg"
```

### 5. Listar Artistas com Paginação
```bash
curl -X GET "http://localhost:8080/api/v1/artist?page=0&size=10&sort=name,asc" \
  -H "Authorization: Bearer {seu-access-token}"
```

### 6. Sincronizar Regionais
```bash
curl -X POST http://localhost:8080/api/sync/regionais
```

### 7. Renovar Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "{seu-refresh-token}"
  }'
```

## 🔔 WebSocket - Notificações em Tempo Real

### Conectar ao WebSocket
```javascript
// Usando SockJS e STOMP
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Inscrever-se no tópico de notificações
    stompClient.subscribe('/topic/notifications', function(notification) {
        const message = JSON.parse(notification.body);
        console.log('Nova notificação:', message);
    });
});
```

### Notificações Automáticas
- Ao criar um novo álbum, todos os clientes conectados recebem uma notificação
- Formato da notificação:
```json
{
  "message": "Novo álbum criado: {título do álbum}",
  "timestamp": "2026-02-05T10:30:00"
}
```

## 🐳 Docker

### Dockerfile
O projeto já possui um Dockerfile configurado para build da aplicação.

```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### Criar Imagem
```bash
./mvnw clean package -DskipTests
docker build -t backend-api:latest .
```

### Executar Container
```bash
docker run -d -p 8080:8080 \
  --name backend-api \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/api-albuns \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e MINIO_ENDPOINT=http://host.docker.internal:9000 \
  backend-api:latest
```

## 📊 Modelo de Dados

### Relacionamentos
```
Artist (1) ←→ (N) Album (1) ←→ (N) AlbumImage
                                      
User (tabela independente para autenticação)

Regional (tabela independente para sincronização)
```

### Principais Entidades

#### Artist
- `id` (Long): Identificador único
- `name` (String): Nome do artista
- `albums` (List<Album>): Lista de álbuns

#### Album
- `id` (Long): Identificador único
- `title` (String): Título do álbum
- `artist` (Artist): Artista relacionado
- `images` (List<AlbumImage>): Imagens do álbum

#### AlbumImage
- `id` (Long): Identificador único
- `fileKey` (String): Chave do arquivo no MinIO
- `isDefault` (Boolean): Se é a imagem padrão do álbum
- `album` (Album): Álbum relacionado

#### User
- `id` (Long): Identificador único
- `username` (String): Nome de usuário
- `password` (String): Senha criptografada
- `role` (Enum): USER ou ADMIN

## 🧪 Testes

### Executar Testes
```bash
# Executar todos os testes
./mvnw test

# Executar com relatório de cobertura
./mvnw clean test jacoco:report
```

## 🚀 Deploy

### Variáveis de Ambiente para Produção
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
MINIO_ENDPOINT=<minio-url>
MINIO_ACCESS_KEY=<access-key>
MINIO_SECRET_KEY=<secret-key>
JWT_SECRET=<chave-secreta-forte>
JWT_EXPIRATION=<tempo-em-ms>
CORS_ALLOWED_ORIGINS=<urls-permitidas>
```

## 📝 Notas Importantes

### Segurança
- **JWT Secret**: Em produção, utilize uma chave forte e guarde em um gerenciador de secrets
- **CORS**: Configure adequadamente as origens permitidas
- **Rate Limiting**: Bucket4j está configurado para prevenir abuso de APIs
- **Upload de Arquivos**: Limite de 10MB por arquivo configurado

### MinIO
- Certifique-se de criar o bucket `album-images` antes de fazer upload de imagens
- As imagens são armazenadas com nomes únicos (UUID)
- Imagens são automaticamente removidas do MinIO ao deletar do banco

### Flyway
- Migrações são executadas automaticamente no startup
- Nunca modifique scripts já aplicados
- Crie novos scripts com versão incremental (V9__, V10__, etc.)

### WebSocket
- Endpoint WebSocket: `/ws`
- Tópico de notificações: `/topic/notifications`
- Requer biblioteca SockJS e STOMP no frontend

## 👨‍💻 Autor

Anthony de Oliveira Vergara


## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

⭐ Se você gostou deste projeto, considere dar uma estrela no repositório!
