# KuenyaWZ API

Spring Boot API for KuenyaWZ project.

## Main Repository

This project is to be implemented for our **[KuenyaWZ](https://github.com/vianneynara/kuenyawz)** site.

## Features/checklist

- [ ] Entities - Controllers - Services - Repositories
    - [x] Account
    - [x] Product
    - [x] Variant (subset of Product)
    - [x] Product Images
    - [ ] Order
    - [ ] Order Item (subset of Order)
    - [ ] Transaction
    - [ ] Likes
    - [ ] CustomSchedule
- [x] Authentication/Authorization
    - [ ] ~~Basic~~
    - [x] JWT

Should be noted that we'll need to implement integration of methods
for the actual website business logic.

## API documentation

Refer to this **[Swagger Documentation](https://app.swaggerhub.com/apis/Nara-ff7/kuenyawz-api/1.0.0#/)**

## Prerequisites

- Java JDK 21 (This project has not been compiled to JAR yet)
- Maven 3.9.9+
- PostgreSQL XX.XX
- ~~Docker~~

## Default configurations

- Database: H2 in memory
- Port: 8081
- Base URL: `http://localhost:8081/api`

## Installing and running application

1. Clone this repository
2. Change directory to the cloned repository
3. Run `mvn spring-boot:run`

### Running with specific profile

Make sure the profile requirement is met. For example, to run with postgres profile,
please make sure you have PostgreSQL installed and running with the provided `.ENV`.

1. Add profile to environment variable: `set SPRING_PROFILES_ACTIVE=postgres`
2. Run `mvn spring-boot:run`

## Authenticating

Environment requirements:

|key|description|
|-|-|
|JWT_SECRET|Base 64 secret key|
JWT_ACCESS_EXP_SECONDS|Token availability until expiration (in seconds), default=3600|
REFRESH_TOKEN_EXP_DAYS|Refresh token availability until expiration (in days), default=7|

Authentication process:
1. `POST /api/auth/register` Registration to create new account and retrieve authententication response
2. `POST /api/auth/login` Login to authenticate to an account and retrieve authententication response
3. `GET /api/auth/me` Me to retrieve current access token's user details/account/owner
4. `POST /api/auth/refresh` Refresh to get new access token (JWT token)
5. `POST /api/auth/revoke` Revoke to invalidate a refresh tokenn (used when logging out)

The refresh token is generated using Java's `SecureRandom` and ancoded using Base64.
