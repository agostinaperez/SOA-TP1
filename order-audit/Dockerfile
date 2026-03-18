# =========================
# IMPORTANTE (PRE-REQUISITO)
# =========================
# Antes de construir la imagen Docker, es OBLIGATORIO generar el .jar
# ejecutando el siguiente comando en la raíz del proyecto:
#
#     mvn clean package
#
# Esto generará el archivo:
#     target/order-audit-0.0.1-SNAPSHOT.jar
#
# Si este archivo no existe, el build de Docker fallará en la instrucción COPY.


# =========================
# IMAGEN BASE
# =========================
# Imagen liviana con Java 21 (JDK incluido)
FROM eclipse-temurin:21-jdk-jammy


# =========================
# DIRECTORIO DE TRABAJO
# =========================
# Carpeta interna del contenedor donde se ejecutará la app
WORKDIR /app


# =========================
# COPIA DEL ARTEFACTO
# =========================
# Copia el .jar generado por Maven desde el host al contenedor
COPY target/order-audit-0.0.1-SNAPSHOT.jar app.jar


# =========================
# PUERTO
# =========================
# Puerto utilizado por la aplicación (informativo)
#EXPOSE 8080


# =========================
# COMANDO DE EJECUCIÓN
# =========================
# Ejecuta la aplicación Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]