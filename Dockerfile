# =====================================================
# Dockerfile — Backend Spring Boot
# Build em duas etapas (multi-stage):
#   1. Maven compila o .jar
#   2. JRE mínimo executa o .jar
# =====================================================

# ── Etapa 1: Build ────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro e baixa dependências
# (camada cacheada — só refaz se o pom mudar)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o código-fonte e compila
COPY src ./src
RUN mvn package -DskipTests -q

# ── Etapa 2: Runtime ──────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o .jar gerado na etapa 1
COPY --from=build /app/target/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Ponto de entrada — ativa o perfil de produção
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
