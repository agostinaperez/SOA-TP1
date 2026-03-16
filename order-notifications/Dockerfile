#Acá hay que ejecutar docker build -t order-notifications .

#Hago multistage building, para compilar mi código acá mismo y después poder ejecutarlo en produccion
# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copiar archivos Maven primero (para cache de dependencias)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

# Compilar
RUN ./mvnw dependency:go-offline -B && \
    ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

LABEL maintainer="Agos"

WORKDIR /app

ARG UID=1000
ARG GID=1000
# instalar curl para healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copiar JAR desde el build stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown 1000:1000 /app/app.jar

ENV JAVA_OPTS="" \
    SERVER_PORT=8083 \
    KAFKA_BOOTSTRAP_SERVERS=broker:9092

EXPOSE 8083

# healthcheck usando actuator (lo añado en las dependencias al actuator)
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

