# Mock REST API

This project has been created to create Mocks of REST APIs.

### Pre-requisites

- Java 11
- GraalVM 22.3.r17-grl
- Docker

### Install tools

We recommend to use SDKman for the installation of Java versions

```shell
sdk install java 22.3.r17-grl
```

You can list the available candidates for GraalVM with the command:

```shell
> sdk list java | grep grl

 GraalVM       |     | 22.3.r19     | grl     |            | 22.3.r19-grl        
               | >>> | 22.3.r17     | grl     | installed  | 22.3.r17-grl        
               |     | 22.3.r11     | grl     |            | 22.3.r11-grl 
```

### Run MockAPI

```shell
./gradlew bootRun
```

### Documents

You can refer to file [HOW_TO_USE.md](docs/HOW_TO_USE.md "How to use guide") for more information
regarding how to create API configurations.

Inside the `docs` folder there also a sample collection to start working with `mock-api`

