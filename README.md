# Saga Pattern Spring Boot Demo

Demonstration of the Saga Orchestration design pattern using Spring Boot and Kafka.

## Requirements

- Java 17
- Internet access to Maven Central on the first execution

## Build and test

The repository includes the official Maven Wrapper in script-only mode, so a
global Maven installation is not required. From the repository root, run:

```powershell
.\mvnw.cmd test
```

On macOS or Linux, run:

```sh
./mvnw test
```

The reactor builds `core`, `orders-service`, `products-service`,
`payments-service`, and `credit-card-processor-service` with Java 17.
