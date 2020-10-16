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
Can search File System or GitHub.

1. File System
CONFIG_ENV: native
SEARCH_LOCATIONS: absoulte file path

2. GitHub
CONFIG_ENV: git
SEARCH_LOCATIONS: git URL
VIRNECT_ENV: develop or staging or production or onpremise

* Environment Variables
- CONFIG_ENV: git or native (default: git)
- SEARCH_LOCATIONS: absolute file path or git URL (default: https://github.com/virnect-corp/PF-Configurations)
- VIRNECT_ENV: develop or staging or production or onpremise (default: develop)

* GitHub Configurations Repository
- https://github.com/virnect-corp/PF-Configurations
```

## Running the application

```shell script
1. File System
ex)
java -DCONFIG_ENV=native -DSEARCH_LOCATIONS=file://file/route -jar ${PF-Download-1.0.jar}

Linux
must be start with 'file://'
Windows
must be start with 'file:///'

2. GitHub
ex)
java -DCONFIG_ENV=git -DVIRNECT_ENV=production -jar ${PF-Download-1.0.jar}

```

#### Run application as docker container via docker image

```shell script
1. File System
ex)
docker run -d --name pf-config -p 6383:6383 -e CONFIG_ENV=native -e SEARCH_LOCATIONS=/usr/app/config -v /config/files/path:/usr/app/config pf-config

2. GitHub
ex)
docker run -d --name pf-config -p 6383:6383 -e CONFIG_ENV=git -e VIRNECT_ENV=production pf-config
```
