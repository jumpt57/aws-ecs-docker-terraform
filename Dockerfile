FROM maven:3.8.7-openjdk-18

EXPOSE 8080
EXPOSE 9090

ARG ENVIRONMENT
ARG DB_URL_SHORTENER_URI
ARG DB_URL_SHORTENER_USER
ARG DB_URL_SHORTENER_PWD

WORKDIR /app

COPY target/urlshortener-1.0.1.jar .

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=$ENVIRONMENT", "urlshortener-1.0.1.jar"]