FROM eclipse-temurin:21-jre AS app
WORKDIR /app
# This copies your compiled JAR into the /app folder
COPY sysh-server/target/sysh-server-*.jar app.jar
# This copies the .env into the /app folder
COPY .env .env 
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM postgres:latest AS postgres
COPY sysh-server/schema.sql /docker-entrypoint-initdb.d/schema.sql