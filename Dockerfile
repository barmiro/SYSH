FROM eclipse-temurin:21 AS app
COPY sysh-server/target/sysh-server-*.jar app.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM postgres:17-bookworm AS postgres
COPY sysh-server/schema.sql /docker-entrypoint-initdb.d/schema.sql
