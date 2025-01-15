FROM openjdk:21 as app
COPY target/sysh-server-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080

FROM postgres:latest as postgres
COPY sysh-server/schema.sql /docker-entrypoint-initdb.d/schema.sql
EXPOSE 5432