FROM openjdk:21 as app
COPY target/sysh-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080

FROM postgres:latest as postgres



