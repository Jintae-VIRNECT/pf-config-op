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

## Clean
```
$ ./gradlew clean
```

## Build

```
$ ./gradlew build -x test
```

## Description
```
Config Server search first by SEARCH_LOCATIONS and then in GITHUB.

SEARCH_LOCATIONS : absoulte file path

Linux
must be start with 'file://'

Windows
must be start with 'file:///'

GITHUB
https://github.com/virnect-corp/PF-Configurations
```

## Running the application

```shell script
#Example: java - Dspring.profiles.active=develop -jar PF-Download-1.0.jar
java -Dspring.profiles.active=${profile env value} -DSEARCH_LOCATIONS=${SEARCH_LOCATIONS} -jar ${PF-Download-1.0.jar}
```

#### Run application as docker container via docker image

```shell script
VIRNECT_ENV = develop, staging, production, onpremise

docker run -d --name pf-config -p ${HOST_PORT}:6383 -e VIRNECT_ENV=${VIRNECT_ENV} -e SEARCH_LOCATIONS=${SEARCH_LOCATIONS} -v ${CONFIG DIR}:${SEARCH_LOCATIONS} pf-config

ex)
docker run -d --name pf-config -p 6383:6383 -e VIRNECT_ENV=develop -e "SEARCH_LOCATIONS=/config" -v /usr/var/configs:/config pf-config


```
