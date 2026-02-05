# Sistema de Autenticação JWT - Backend

## Implementação Realizada

### 1. Segurança (CORS)
- **Configuração restrita**: Apenas domínios configurados em `cors.allowed-origins` têm acesso
- **Padrão**: `http://localhost:4200`
- **Customização**: Editar `application.properties` para adicionar mais domínios (separados por vírgula)

### 2. Autenticação JWT
- **Expiração**: 5 minutos (300000ms)
- **Renovação**: Endpoint `/api/auth/refresh` para renovar token
- **Secret Key**: Configurado em `application.properties`

### 3. Estrutura Criada

#### Migration
- **V7__create_user_table.sql**: Tabela `users` com usuário admin padrão
  - Username: `admin`
  - Password: `admin123`
  - Role: `ADMIN`

#### Models
- **User**: Entidade com roles (USER, ADMIN)

#### Security
- **JwtService**: Geração e validação de tokens JWT
- **JwtAuthenticationFilter**: Filtro para autenticação via Bearer token
- **SecurityConfig**: Configuração de segurança Spring

#### Endpoints de Autenticação
```
POST /api/auth/register
Body: { "username": "string", "password": "string" }
Response: { "token": "jwt", "username": "string", "role": "USER" }

POST /api/auth/login
Body: { "username": "string", "password": "string" }
Response: { "token": "jwt", "username": "string", "role": "USER|ADMIN" }

POST /api/auth/refresh
Headers: Authorization: Bearer {token}
Response: { "token": "new_jwt", "username": "string", "role": "USER|ADMIN" }
```

#### Endpoints de Usuários (ADMIN apenas)
```
GET /api/users
Headers: Authorization: Bearer {admin_token}
Response: [ {...users} ]

GET /api/users/{id}
Headers: Authorization: Bearer {admin_token}
Response: { ...user }

DELETE /api/users/{id}
Headers: Authorization: Bearer {admin_token}
Response: 204 No Content
```

### 4. Como Usar

#### Primeiro Login (Admin)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### Registrar Novo Usuário
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"senha123"}'
```

#### Renovar Token (antes de expirar)
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

#### Acessar Endpoints Protegidos
```bash
curl -X GET http://localhost:8080/api/albums \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### 5. Configurações (application.properties)

```properties
# CORS - Domínios permitidos
cors.allowed-origins=http://localhost:4200

# JWT - Secret e tempo de expiração
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=300000
```

### 6. Fluxo de Autenticação

1. Cliente faz login → recebe token JWT (válido por 5 minutos)
2. Cliente usa token no header `Authorization: Bearer {token}`
3. Antes de expirar, cliente renova token via `/api/auth/refresh`
4. Token renovado tem validade de mais 5 minutos

### 7. Roles e Permissões

- **USER**: Acesso aos endpoints de álbuns, artistas e imagens
- **ADMIN**: Acesso total + gerenciamento de usuários

### Próximos Passos

Execute o projeto:
```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

