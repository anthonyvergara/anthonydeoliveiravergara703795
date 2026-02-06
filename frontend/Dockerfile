# Build do Angular dentro do container
FROM node:22 AS build

WORKDIR /app

# Copia todos os arquivos do projeto
COPY . .

# Instala dependências
RUN npm install

# Build do Angular
RUN npm run build

# Servir via Nginx
FROM nginx:alpine

# Copia o build para o Nginx
COPY --from=build /app/dist/frontend /usr/share/nginx/html

# Expõe porta 80
EXPOSE 80

