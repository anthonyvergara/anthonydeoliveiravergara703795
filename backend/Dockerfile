# Imagem base com Java 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia o jar gerado pelo Maven
COPY target/*.jar app.jar

# Expõe a porta da API
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]

