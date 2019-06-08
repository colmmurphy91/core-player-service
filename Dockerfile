FROM openjdk:8-jdk-alpine
ARG key1
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/coreplayerservice
ADD target/core-player-service-$key1.jar coreplayerservice.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/coreplayerservice.jar"]

