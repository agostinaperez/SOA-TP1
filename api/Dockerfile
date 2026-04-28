FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG UID=1000
ARG GID=1000

RUN addgroup -S -g ${GID} spring && \
    adduser -S -u ${UID} -G spring spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

ENV JAVA_OPTS="" \
    SERVER_PORT=8081

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]