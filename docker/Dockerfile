FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY ./build/libs/PF-Config-1.0.0.jar ./PF-Config-1.0.0.jar

EXPOSE 6383

## Command executed when container is start up.
CMD ["java", "-Xmx400M", "-jar", "PF-Config-1.0.0.jar"]
