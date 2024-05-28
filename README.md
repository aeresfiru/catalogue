# Product Catalogue Project

## Purpose

This project is a pet project aimed at leveling up skills in software development, particularly in building and managing
microservices using the Spring ecosystem. It serves as a learning experience and a practical application of various
technologies and best practices.

## Overview

This project is a product catalogue system designed to allow users to browse products, leave reviews, and manage their
favorite items. Managers have the ability to add, remove, and update products, as well as manage filter lists. Note that
purchasing functionality and customer interface is not implemented in this version. The system is built using a
microservices architecture and leverages several technologies within the Spring ecosystem.

## Technology Stack

- **Spring**: Boot, Webflux, Data, Security, Cloud, Boot Admin
- **Frontend**: Thymeleaf
- **Databases**: PostgreSQL, MongoDB
- **Testing**: WireMock, Testcontainers, AssertJ, REST Docs
- **API Documentation**: OpenAPI/Swagger
- **Miscellaneous**: Lombok, Spring Validation
- **Containerization**: Docker

## Getting Started

### Prerequisites

- Java 21+
- Docker
- PostgreSQL and MongoDB

### Setup

- Clone the Repository

```bash
  git clone git@github.com:jija-a/catalogue.git
  cd catalogue
```

### Running the Services with Docker

- Build the project:

```bash
  ./mvnw clean package
```

- Launch Docker
- Build the Docker images and start the services:

```bash
docker compose up
```

### Configuration without Docker

- Configure Spring Cloud Config Server with your configuration repository or use native.
- Ensure Config Server is running.
- Ensure Eureka Server is running for service discovery.

### Database Setup

Ensure PostgreSQL and MongoDB are running and accessible.
Update the `application.yml` files with your database credentials.

### API Documentation

- Access Swagger UI at:
    - `http://localhost:8081/swagger-ui/index.html` to explore catalogue API endpoints.
    - `http://localhost:8085/webjars/swagger-ui/index.html` to explore feedback API endpoints.

### Running Tests

- Run unit and integration tests:
  ```bash
  ./mvnw verify
  ```

### Contact

For any questions or support, please contact [aeresfiru@proton.me](mailto:aeresfiru@proton.me).