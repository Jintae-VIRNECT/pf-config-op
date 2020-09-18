## Author

```
delbert@virnect.com

```

## Description

```
VIRNECT PLATFORM CONFIGURATION SERVER

```

## Environment

```
java -> openJDk 1.8 ^
Gradle -> gradle 5.* (but, gradle bundle is included this project)
```

## Build

```
$ ./gradlew clean build
```

## Running the application

```shell script
#Example: java - Dspring.profiles.active=develop -jar PF-Download-1.0.jar
java -Dspring.profiles.active=${profile env value} -jar ${PF-Download-1.0.jar}
```

## Running the application with Docker

#### Build docker image from dockerfile

```shell script
docker build -t <imageName>:<tag> ${DockerfilePath} .
```

#### Run application as docker container via docker image

```shell script
docker run -d --name '<container_name>' -p <host_port>:<container_port> ${docker image name}
```
