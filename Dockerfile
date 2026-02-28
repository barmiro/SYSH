FROM eclipse-temurin:21-jre AS app
WORKDIR /app
# Only copy the JAR file
COPY sysh-server/target/sysh-server-*.jar app.jar
# No COPY .env line here!
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM postgres:latest AS postgres
COPY sysh-server/schema.sql /docker-entrypoint-initdb.d/schema.sql