FROM openjdk:21 AS app
COPY sysh-server/target/sysh-server-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080

FROM postgres:latest AS postgres
COPY sysh-server/schema.sql /docker-entrypoint-initdb.d/schema.sql
EXPOSE 5432:5432