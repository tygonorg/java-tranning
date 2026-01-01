# Java Training Project

This is a Spring Boot application for Java training.

## Prerequisites

- **Java 21** (Required)
  - This project is configured to use Java 21 via `gradle.properties`.
  - Ensure you have a JDK 21 installed on your system.
  - The local override in `gradle.properties` points to `/usr/lib/jvm/java-21-openjdk-amd64` by default.

## How to Run

To start the application, run the following command in the project root:

```bash
./gradlew bootRun
```

This will compile the application and start the Spring Boot server.

## How to Build

To build the executable JAR file without running it:

```bash
./gradlew build
```

The artifacts will be generated in the `build/libs` directory.

## API Documentation

Access the API documentation via Swagger UI at:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)