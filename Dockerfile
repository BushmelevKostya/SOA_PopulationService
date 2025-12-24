FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar population-service.jar

EXPOSE 5737

ENTRYPOINT ["java", "-jar", "population-service.jar"]
