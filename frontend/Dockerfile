# Build do Angular dentro do container
FROM node:18 AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

# Servir via Nginx
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
EXPOSE 80

