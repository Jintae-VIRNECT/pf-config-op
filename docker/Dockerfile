### Cache Stage
FROM gradle:jdk11 AS cache

# set workdir
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# set cache folder
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home

# only copy dependency-related files
COPY build.gradle settings.gradle ./
COPY gradle.properties /home/gradle/.gradle
# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true


### Build Stage
FROM gradle:jdk11 AS builder

# set workdir
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# load dependencies from cache
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY gradle.properties /home/gradle/.gradle
# copy all source
COPY . .

# build soruce file with dependnecies
RUN gradle clean build -x test --console=plain -DXms512m -DXmx4096m
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

### Runtime Stage
FROM adoptopenjdk/openjdk11:alpine-slim

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

VOLUME /config

EXPOSE 6383
CMD ["java", "-Xms400M", "-Xmx400M", "org.springframework.boot.loader.JarLauncher"]

# copy build source
#COPY --from=builder ${APP_HOME}/virnect.pfx ./
COPY --from=builder ${APP_HOME}/dependencies/ ./
COPY --from=builder ${APP_HOME}/spring-boot-loader/ ./
COPY --from=builder ${APP_HOME}/snapshot-dependencies/ ./
COPY --from=builder ${APP_HOME}/application/ ./
COPY --from=builder ${APP_HOME}/build.gradle ./

# default app version
ARG APP_VERSION=0.1.0 
# change app version
RUN sed -i "/version =/ c\\version = \'${APP_VERSION}\'" build.gradle
