FROM eclipse-temurin:21-jdk-jammy

LABEL maintainer="Validator Service"

WORKDIR /app

ARG UID=1000
ARG GID=1000

# instala curl para healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g ${GID} spring \
    && useradd -m -u ${UID} -g ${GID} spring

# copia jar generado por maven
COPY target/validator-0.0.1-SNAPSHOT.jar app.jar

RUN chown spring:spring /app/app.jar

USER spring

ENV JAVA_OPTS="" \
    SERVER_PORT=8081 \
    KAFKA_BOOTSTRAP_SERVERS=broker:9092

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]