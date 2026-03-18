#./mvnw clean package -DskipTests
FROM eclipse-temurin:21-jdk-jammy

LABEL maintainer="Agos"

WORKDIR /app

ARG UID=1000
ARG GID=1000

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g ${GID} spring \
    && useradd -m -u ${UID} -g ${GID} spring

COPY target/notification-0.0.1-SNAPSHOT.jar app.jar

RUN chown spring:spring /app/app.jar

USER spring

ENV JAVA_OPTS="" \
    SERVER_PORT=${SERVER_PORT} \
    KAFKA_BOOTSTRAP_SERVERS=broker:29092

EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD pgrep java || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]