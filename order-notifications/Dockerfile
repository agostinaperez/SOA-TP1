#para Dockerizar este microservicio hice:
#mvn -N wrapper:wrapper
#./mvnw clean package -DskipTests
#docker build -t order-notifications .
FROM eclipse-temurin:21-jdk-jammy

LABEL maintainer="Agos"

WORKDIR /app

ARG UID=1000
ARG GID=1000

# instalar curl para healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g ${GID} spring \
    && useradd -m -u ${UID} -g ${GID} spring

# copiar jar
COPY target/notification-0.0.1-SNAPSHOT.jar app.jar

# permisos
RUN chown spring:spring /app/app.jar

USER spring

ENV JAVA_OPTS="" \
    SERVER_PORT=8083 \
    KAFKA_BOOTSTRAP_SERVERS=kafka:9092

EXPOSE 8083

# healthcheck usando actuator (lo añado en las dependencias al actuator)
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

