FROM openjdk:8-jre-slim

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

ARG buildDir=build/unpack

COPY ${buildDir}/lib BOOT-INF/lib
COPY ${buildDir}/app .

EXPOSE 6383

## Command executed when container is start up.
CMD ["java", "-Xmx400M", "org.springframework.boot.loader.JarLauncher"]
