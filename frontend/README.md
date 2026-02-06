# Frontend - Sistema de Gestão de Artistas

## 📋 Descrição do Projeto

Aplicação frontend desenvolvida em Angular 20 para gerenciamento de artistas, álbuns e autenticação, com integração de WebSocket para notificações em tempo real, autenticação JWT e interface moderna com Tailwind CSS.

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Docker e Docker Compose instalados
- Git instalado

### Clonando e Executando

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd mini-erp

# 2. Execute com Docker Compose
docker-compose up --build

# 3. Acesse a aplicação
# API: http://localhost:4200
```

A aplicação será executada automaticamente com:
- ✅ Banco PostgreSQL configurado
- ✅ Migrações Liquibase aplicadas
- ✅ Dados iniciais carregados
- ✅ Repositório MinIO
- ✅ API REST disponível na porta 8080

### Usuário Inicial
```
Admin: username=admin, password=admin123
```

## 🏗️ Arquitetura do Projeto

### Estrutura de Pastas

```
src/
├── app/
│   ├── core/                    # Módulos centrais da aplicação
│   │   ├── auth/               # Sistema de autenticação
│   │   │   ├── dtos/           # Data Transfer Objects
│   │   │   ├── facade/         # Camada de fachada
│   │   │   ├── guards/         # Guards de rota
│   │   │   ├── interceptors/   # HTTP Interceptors
│   │   │   ├── models/         # Modelos de dados
│   │   │   ├── services/       # Serviços de autenticação
│   │   │   └── state/          # Gerenciamento de estado
│   │   ├── health/             # Health checks
│   │   ├── http/               # Configurações HTTP
│   │   └── layout/             # Layout principal
│   │
│   ├── features/               # Funcionalidades da aplicação
│   │   ├── artists/            # Módulo de artistas
│   │   │   ├── components/     # Componentes reutilizáveis
│   │   │   │   ├── album-card/
│   │   │   │   ├── album-form/
│   │   │   │   ├── artist-card/
│   │   │   │   ├── artist-form/
│   │   │   │   ├── cover-preview/
│   │   │   │   └── upload-images/
│   │   │   ├── dtos/           # Data Transfer Objects
│   │   │   ├── facade/         # Camada de fachada
│   │   │   ├── models/         # Modelos de domínio
│   │   │   ├── pages/          # Páginas do módulo
│   │   │   ├── services/       # Serviços de negócio
│   │   │   └── state/          # Gerenciamento de estado
│   │   ├── auth/               # Módulo de autenticação
│   │   │   └── pages/          # Páginas de login/registro
│   │   └── dashboard/          # Módulo de dashboard
│   │       └── pages/          # Páginas do dashboard
│   │
│   ├── shared/                 # Componentes e serviços compartilhados
│   │   ├── animations/         # Animações personalizadas
│   │   │   └── route-animations.ts
│   │   ├── components/         # Componentes reutilizáveis
│   │   │   ├── card/
│   │   │   ├── confirm-modal/
│   │   │   ├── notification-bell/
│   │   │   ├── pagination/
│   │   │   ├── search-input/
│   │   │   ├── table/
│   │   │   ├── toast/
│   │   │   └── upload/
│   │   ├── models/             # Modelos compartilhados
│   │   │   ├── notification.model.ts
│   │   │   └── toast.model.ts
│   │   └── services/           # Serviços compartilhados
│   │       ├── notification.service.ts
│   │       ���── toast.service.ts
│   │
│   ├── app.config.ts           # Configuração da aplicação
│   ├── app.routes.ts           # Configuração de rotas
│   └── app.ts                  # Componente raiz
│
├── environments/               # Configurações de ambiente
│   ├── environment.prod.ts
│   └── environment.ts
│
├── index.html
├── main.ts
├── polyfills.ts
└── styles.scss                 # Estilos globais
```

### Princípios Arquiteturais

A aplicação segue uma arquitetura modular baseada em boas práticas do Angular, promovendo:

- **Separação de Responsabilidades**: Organização clara entre core, features e shared
- **Arquitetura em Camadas**: 
  - **Facade**: Abstração da lógica de negócio
  - **Services**: Comunicação com APIs
  - **State**: Gerenciamento de estado reativo
  - **Components**: Apresentação e interação com usuário
- **Reutilização de Código**: Componentes e serviços compartilhados
- **Escalabilidade**: Estrutura modular que facilita adição de novas funcionalidades
- **Manutenibilidade**: Código organizado e de fácil compreensão

#### Camadas da Aplicação

- **Core**: Funcionalidades essenciais da aplicação (auth, layout, http)
- **Features**: Módulos de funcionalidades específicas (artists, dashboard)
- **Shared**: Componentes, serviços e utilitários reutilizáveis
- **DTOs**: Contratos de dados com o backend
- **Models**: Representação de entidades do domínio
- **Facade**: Orquestração de serviços e lógica de negócio
- **State**: Gerenciamento reativo de estado da aplicação

## 🔧 Tecnologias e Recursos

### Stack Principal
- **Angular 20.1** - Framework frontend
- **TypeScript** - Linguagem de programação
- **RxJS 7.8** - Programação reativa
- **Tailwind CSS 3.4** - Framework CSS utilitário
- **Angular Material 20.1** - Componentes UI
- **Lucide Angular** - Ícones
- **Docker** - Containerização
- **Nginx** - Servidor web para produção

### Recursos Implementados

#### 🔐 **Autenticação e Autorização**
- JWT (JSON Web Token)
- Guards de rota
- HTTP Interceptors
- Refresh tokens
- Gerenciamento de estado de autenticação

#### 🔔 **Notificações em Tempo Real**
- **WebSocket** com STOMP.js
- **SockJS** para fallback
- Sistema de notificações em tempo real
- Componente de sino de notificações

#### 🎨 **Interface do Usuário**
- Design responsivo com Tailwind CSS
- Componentes reutilizáveis
- Animações de transição de rotas
- Sistema de toast para feedback
- Modais de confirmação
- Tabelas com paginação
- Componentes de upload de imagens

#### 📊 **Funcionalidades de Negócio**
- Gestão de artistas
- Gestão de álbuns
- Upload de imagens
- Preview de capas
- Sistema de busca
- Paginação de dados

#### ⚡ **Performance e Qualidade**
- Lazy loading de módulos
- Change detection otimizada
- Gerenciamento eficiente de estado
- Build otimizado para produção
- Compression e minificação

## 🎯 Funcionalidades Principais

### Sistema de Autenticação
- Login e registro de usuários
- Proteção de rotas
- Gerenciamento de tokens
- Logout automático em caso de token expirado

### Gestão de Artistas
- Listagem de artistas
- Criação e edição de artistas
- Upload de fotos de artistas
- Cards visuais com informações

### Gestão de Álbuns
- Listagem de álbuns
- Criação e edição de álbuns
- Upload de capas de álbuns
- Preview de imagens
- Associação com artistas

### Sistema de Notificações
- Notificações em tempo real via WebSocket
- Sino de notificações com contador
- Histórico de notificações
- Marcação de leitura

## 🔄 Integração com Backend

A aplicação se comunica com uma API REST backend através de:

- **HTTP Services**: Serviços Angular para chamadas HTTP
- **Interceptors**: Adição automática de tokens JWT
- **DTOs**: Contratos de dados tipados
- **Error Handling**: Tratamento centralizado de erros

### Configuração de Ambiente

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'http://localhost:8080/ws'
};
```

## 📦 Scripts Disponíveis

```bash
npm start          # Inicia servidor de desenvolvimento (porta 4200)
npm run build      # Build de produção
npm run watch      # Build em modo watch
npm test           # Executa testes unitários
npm run ng         # Acessa Angular CLI
```

## 🐳 Docker

### Dockerfile Multi-stage

O projeto utiliza um Dockerfile multi-stage para otimização:

1. **Stage 1 (Build)**: Compila a aplicação Angular
2. **Stage 2 (Production)**: Serve os arquivos estáticos via Nginx

### Vantagens:
- Imagem final leve (baseada em nginx:alpine)
- Build consistente em qualquer ambiente
- Fácil deploy em qualquer plataforma

### Boas Práticas

- Use DTOs para tipagem de dados da API
- Implemente facades para lógica de negócio complexa
- Mantenha componentes pequenos e focados
- Utilize services para comunicação com backend
- Implemente state management para dados compartilhados
- Reutilize componentes shared sempre que possível

## 📱 Responsividade

A aplicação é totalmente responsiva, utilizando:
- Tailwind CSS utilities
- Grid e Flexbox layouts
- Breakpoints mobile-first
- Componentes adaptáveis

## 🔒 Segurança

- Proteção de rotas sensíveis com Guards
- Tokens JWT armazenados de forma segura
- Sanitização de inputs
- Validação de formulários
- Interceptors para tratamento de erros de autenticação

## 👨‍💻 Autor

Anthony de Oliveira Vergara


## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

Desenvolvido com ❤️ usando Angular e Tailwind CSS

