FROM openjdk:17-bullseye

WORKDIR /app

COPY target/*.jar application.jar

ENTRYPOINT [ "java", "-jar", "application.jar" ]

EXPOSE 8080
