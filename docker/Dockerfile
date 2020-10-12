# Build Stage
FROM openjdk:8 AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Runtime Stage
FROM openjdk:8-jre-alpine

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
ARG buildDir=build/unpack

COPY --from=builder ${buildDir}/lib BOOT-INF/lib
COPY --from=builder ${buildDir}/app .

EXPOSE 6383
CMD ["java", "-XX:+UseG1GC", "-Xmx400M", "org.springframework.boot.loader.JarLauncher"]