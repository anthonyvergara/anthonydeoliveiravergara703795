# Sistema de Autenticação - Music Artist Manager

## 📋 Visão Geral

Sistema completo de autenticação implementado seguindo a arquitetura do projeto, com suporte a JWT tokens, refresh automático e proteção de rotas.

## 🏗️ Arquitetura

### Core Auth (`src/app/core/auth/`)
Infraestrutura global de autenticação:

```
core/auth/
├── dtos/
│   ├── login-request.dto.ts      # DTO para requisição de login
│   ├── token-response.dto.ts     # DTO para resposta com tokens
│   └── refresh-request.dto.ts    # DTO para renovação de token
│
├── models/
│   └── auth.model.ts              # Interfaces (AuthUser, AuthTokens, AuthState)
│
├── services/
│   └── auth.service.ts            # HTTP service (chamadas à API)
│
├── state/
│   └── auth.state.ts              # BehaviorSubject para estado reativo
│
├── facade/
│   └── auth.facade.ts             # Facade pattern (camada de negócio)
│
├── guards/
│   └── auth.guard.ts              # Guard para proteção de rotas
│
└── interceptors/
    └── auth.interceptor.ts        # Interceptor HTTP para tokens
```

### Feature Auth (`src/app/features/auth/`)
Página de login:

```
features/auth/pages/login/
├── login.component.html
├── login.component.ts
└── login.component.scss
```

## 🔐 Fluxo de Autenticação

### 1. Login
```typescript
POST /api/auth/login
Body: { username, password }
Response: { accessToken, refreshToken, username, role }
```

### 2. Refresh Token (Automático)
- Access Token expira em 5 minutos
- Refresh Token expira em 30 minutos
- Quando uma requisição retorna 401, o interceptor automaticamente:
  1. Chama o endpoint de refresh
  2. Obtém novos tokens
  3. Repete a requisição original com o novo token

```typescript
POST /api/auth/refresh
Body: { refreshToken }
Response: { accessToken, refreshToken, username, role }
```

### 3. Autorização
Todas as requisições (exceto login e refresh) incluem automaticamente:
```
Authorization: Bearer {accessToken}
```

## 🛡️ Proteção de Rotas

Todas as rotas (exceto `/login`) estão protegidas pelo `authGuard`:

```typescript
{
  path: 'artists',
  loadComponent: () => import('...'),
  canActivate: [authGuard]  // ← Protege a rota
}
```

Se o usuário não estiver autenticado:
- Redireciona para `/login`
- Salva a URL original em `returnUrl`
- Após login bem-sucedido, retorna para a URL original

## 💾 Persistência

Os tokens são salvos no `localStorage`:
- Chave: `auth_tokens`
- Ao recarregar a página, a autenticação é mantida

## 🎨 Interface de Login

Tela de login responsiva com:
- Formulário reativo (username + password)
- Validação em tempo real
- Mensagens de erro
- Loading state
- Design moderno com Tailwind CSS

## 📦 Componentes Atualizados

### App Component
- Exibe/oculta sidebar baseado no estado de autenticação
- Mostra nome do usuário no header
- Botão de logout funcional

## 🚀 Como Usar

### 1. Fazer Login
Acesse `/login` e entre com suas credenciais.

### 2. Logout
Clique no botão de logout no header (ícone de saída).

### 3. Token Expirado
O sistema renova automaticamente. Usuário não percebe.

## 📝 Endpoints da API

Configure a URL base em `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',  // ← Sua API aqui
  // ...
};
```

## 🔧 Configuração

### Interceptor
Configurado em `app.config.ts`:
```typescript
provideHttpClient(
  withInterceptors([authInterceptor])
)
```

### Guards
Aplicados nas rotas em `app.routes.ts`:
```typescript
canActivate: [authGuard]
```

## ✅ Checklist de Implementação

- ✅ DTOs (Login, Refresh, Token Response)
- ✅ Models (AuthUser, AuthTokens, AuthState)
- ✅ Service HTTP (login, refresh)
- ✅ State Management (BehaviorSubject)
- ✅ Facade (lógica de negócio)
- ✅ Guard (proteção de rotas)
- ✅ Interceptor (auto-refresh + headers)
- ✅ Componente de Login
- ✅ Integração com App Component
- ✅ Logout funcional
- ✅ Persistência de tokens

## 🎯 Próximos Passos

1. Teste com seu backend
2. Ajuste as URLs se necessário
3. Personalize mensagens de erro
4. Adicione mais validações se necessário

## 📚 Tecnologias

- Angular 17+ (Standalone Components)
- RxJS (BehaviorSubject)
- Tailwind CSS
- TypeScript
- JWT Authentication

