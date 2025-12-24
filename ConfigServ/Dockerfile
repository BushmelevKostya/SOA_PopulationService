FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/ConfigService-*.jar config-service.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "config-service.jar"]
