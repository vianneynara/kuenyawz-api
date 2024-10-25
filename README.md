# KuenyaWZ API

Spring Boot API for KuenyaWZ project.

## Main Repository

This project is to be implemented for our **[KuenyaWZ](https://github.com/vianneynara/wz-snack-n-bites)** site.

## Features/checklist

- [ ] Entities - Controllers - Services - Repositories
    - [x] Account
    - [ ] Product
    - [ ] Variant (subset of Product)
    - [ ] Order
    - [ ] Order Item (subset of Order)
    - [ ] Transaction
    - [ ] Likes
    - [ ] CustomSchedule
- [ ] Authentication/Authorization
    - [ ] Basic
    - [ ] JWT

Should be noted that we'll need to implement integration of methods
for the actual website business logic.

## API documentation

Refer to this **[Swagger Documentation](https://app.swaggerhub.com/apis-docs/NaraNarwandaru/wz-snack_and_bites_api/1.0.0)**

## Prerequisites

- Java JDK 21 (This project has not been compiled to JAR yet)
- Maven 3.9.9+
- PostgreSQL XX.XX
- ~~Docker~~

## Default configurations

- Database: H2 in memory
- Port: 8081
- Base URL: `http://localhost:8081/api/v1`

## Installing and running application

1. Clone this repository
2. Change directory to the cloned repository
3. Run `mvn spring-boot:run`