# 📐 Arquitetura do Projeto - Music Artist Manager

## 🏗️ Estrutura Geral

Este projeto segue uma arquitetura modular baseada em features, utilizando Angular 17+ com standalone components e signals para gerenciamento de estado reativo.

## 📂 Estrutura de Diretórios

```
src/
├── app/
│   ├── core/                          # Módulos core da aplicação
│   │   ├── auth/                      # Autenticação
│   │   ├── health/                    # Health checks
│   │   ├── http/                      # Interceptors e HTTP config
│   │   └── layout/                    # Layout components
│   │
│   ├── features/                      # Features do sistema
│   │   ├── artists/                   # Feature de Artistas
│   │   │   ├── components/            # Componentes reutilizáveis
│   │   │   │   ├── album-card/        # Card de álbum
│   │   │   │   ├── album-form/        # Formulário de criação de álbum
│   │   │   │   ├── artist-card/       # Card de artista
│   │   │   │   ├── artist-form/       # Formulário de criação de artista
│   │   │   │   └── cover-preview/     # Preview de capa
│   │   │   │
│   │   │   ├── dtos/                  # Data Transfer Objects
│   │   │   │   ├── album-images-response.dto.ts
│   │   │   │   ├── album-response.dto.ts
│   │   │   │   └── artist-response.dto.ts
│   │   │   │
│   │   │   ├── facade/                # Facade pattern
│   │   │   │   └── artist.facade.ts   # Fachada para state management
│   │   │   │
│   │   │   ├── models/                # Domain models
│   │   │   │   ├── album.model.ts
│   │   │   │   └── artist.model.ts
│   │   │   │
│   │   │   ├── pages/                 # Páginas/Containers
│   │   │   │   ├── artist-list/       # Lista de artistas
│   │   │   │   └── artist-detail/     # Detalhes do artista
│   │   │   │
│   │   │   ├── services/              # Services
│   │   │   │   └── artist.service.ts  # API integration
│   │   │   │
│   │   │   └── state/                 # State management
│   │   │       └── artist.state.ts    # Estado global de artistas
│   │   │
│   │   ├── auth/                      # Feature de Autenticação
│   │   └── dashboard/                 # Feature de Dashboard
│   │       └── pages/
│   │           └── home/              # Página inicial
│   │
│   ├── shared/                        # Recursos compartilhados
│   │   ├── animations/                # Animações reutilizáveis
│   │   │   └── route-animations.ts    # Animações de rota
│   │   │
│   │   ├── components/                # Componentes compartilhados
│   │   │   ├── card/                  # Card genérico
│   │   │   ├── confirm-modal/         # Modal de confirmação
│   │   │   ├── pagination/            # Componente de paginação
│   │   │   ├── search-input/          # Input de busca
│   │   │   ├── table/                 # Tabela genérica
│   │   │   ├── toast/                 # Sistema de notificações
│   │   │   └── upload/                # Upload de arquivos
│   │   │
│   │   ├── models/                    # Models compartilhados
│   │   │   └── toast.model.ts         # Interface de toast
│   │   │
│   │   └── services/                  # Services compartilhados
│   │       └── toast.service.ts       # Serviço de notificações
│   │
│   ├── app.config.ts                  # Configuração da aplicação
│   ├── app.routes.ts                  # Configuração de rotas
│   ├── app.html                       # Template principal
│   ├── app.scss                       # Estilos globais
│   └── app.ts                         # Componente raiz
│
├── environments/                      # Variáveis de ambiente
│   ├── environment.ts                 # Desenvolvimento
│   └── environment.prod.ts            # Produção
│
└── public/                            # Assets estáticos
    └── assets/
        └── artists/                   # Imagens de artistas
```

MEU FUTURO SISTEMA:

Esse sistema ira se integrar com o back-end via API RESTful.
Será desenvolvido em Angular com TypeScript, seguindo boas práticas de arquitetura e organização de código.
Utilizarei conceitos de Clean Architecture adaptados para o front-end, separando responsabilidades em camadas distintas.

Descricao do projeto:
Sistema de gerenciamento de artistas e álbuns musicais.
A) Tela Inicial - Listagem de Artistas
Consultar e exibir lista de artistas;
Exibir em cards;
Campo de busca por nome, ordenação asc/desc;
Paginação;

B) Tela de Detalhamento do Artista
Ao clicar em artista, exibir álbuns associados;
Exibir informações completas, incluindo capas;
Se não houver álbuns, exibir mensagem.

C) Tela de Cadastro/Edição
Formulário para inserir artistas;
Formulário para adicionar álbuns a um artista;
Edição de registros;
Upload de capas.

D) Autenticação
Acesso ao front exige login;
Implementar autenticação JWT consumindo o endpoint;
Gerenciar expiração e renovação do token.

E) Arquitetura
Boas práticas (modularização, componentização, services);
Layout responsivo;
Lazy Loading Routes para módulos distintos;
Paginação;

---------------------------------------------------------------

No Front end, padrão Facade e gestão de estado com BehaviorSubject.
Health Checks e Liveness/Readiness.

--------------------------------------------------------------

VISÃO GERAL DA ARQUITETURA
src/
├── app/
│   ├── core/
│   ├── shared/
│   ├── features/
│   ├── app.component.*
│   └── app-routing.module.ts
│
├── assets/
├── environments/
└── styles.css


CORE — infraestrutura global (1 vez no app)
Tudo que é transversal e não pertence a uma feature específica.

core/
├── auth/
│   ├── auth.facade.ts
│   ├── auth.service.ts
│   ├── auth.state.ts
│   ├── auth.guard.ts
│   ├── auth.interceptor.ts
│   └── auth.model.ts
│
├── http/
│   ├── error.interceptor.ts
│   └── loading.interceptor.ts
│
├── health/
│   ├── health.service.ts
│   ├── health.model.ts
│   └── health.facade.ts
│
├── layout/
│   ├── header/
│   ├── sidebar/
│   └── layout.component.ts
│
└── core.module.ts


Atende:
🔐 Autenticação JWT + refresh
🚦 Guards
🌐 Interceptors
❤️ Health / Liveness / Readiness
🧭 Layout base (header/sidebar)

SHARED — reutilizável, sem regra de negócio
shared/
├── components/
│   ├── card/
│   ├── table/
│   ├── pagination/
│   ├── search-input/
│   ├── confirm-modal/
│   └── upload/
│
├── models/
│   ├── page.model.ts
│   └── sort.model.ts
│
├── dtos/
│   └── api-response.dto.ts
│
├── pipes/
├── directives/
└── shared.module.ts


Atende:
Cards/tabelas responsivas
Paginação reutilizável
Campo de busca
Upload genérico (MinIO)
Tailwind aplicado aqui

FEATURES — domínio do sistema (Clean no front)
features/
├── auth/
├── artists/
└── dashboard/


Cada feature:
É Lazy Loaded
Tem models, dtos, service, facade e state próprios


FEATURE: AUTH (Lazy)
features/auth/
├── pages/
│   └── login/
│       └── login.component.*
│
├── dtos/
│   ├── login-request.dto.ts
│   └── token-response.dto.ts
│
├── auth-routing.module.ts
└── auth.module.ts


Atende:
Login obrigatório
Consumo de endpoint JWT
Renovação via auth.facade (core)


FEATURE: ARTISTS (principal do sistema)
Estrutura completa

features/artists/
├── pages/
│   ├── artist-list/
│   │   └── artist-list.component.*
│   │
│   ├── artist-detail/
│   │   └── artist-detail.component.*
│   │
│   └── artist-form/
│       └── artist-form.component.*
│
├── components/
│   ├── artist-card/
│   ├── album-card/
│   ├── album-form/
│   └── cover-preview/
│
├── models/
│   ├── artist.model.ts
│   └── album.model.ts
│
├── dtos/
│   ├── artist-response.dto.ts
│   ├── create-artist.dto.ts
│   ├── update-artist.dto.ts
│   └── create-album.dto.ts
│
├── services/
│   └── artist.service.ts        ← HTTP PURO
│
├── state/
│   └── artist.state.ts          ← BehaviorSubjects
│
├── facade/
│   └── artist.facade.ts         ← regra do front
│
├── artists-routing.module.ts
└── artists.module.ts



FLUXO DE LOGIN:

Para não forçar o usuário a logar toda hora: 
O login você gera dois tokens: 
Access Token → expira em 5 minutos 
Refresh Token → expira em 30 minutos
Fluxo: 
Access Token expira Cliente chama um endpoint específico de renovação Envia o Refresh Token Backend valida: se é legítimo se não foi revogado Backend gera um novo Access Token e Refresh Token.
O Usuário continua logado sem perceber.
