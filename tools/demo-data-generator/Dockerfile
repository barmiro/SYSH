FROM openjdk:21 AS app
COPY target/demo-data-generator-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "/app.jar"]
