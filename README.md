# Bookshelf - book's management app

Backend application that's provide book's management system.

App tracks lifecycle of every book. There are borrows, reservations, 
book's categories, book's owners, feign client, transactions and scheduled logging.

Unit and integration tests without Spring context.

Prod, test database setups. More about it is included in Setup Guide file (.pdf, .odt)

This app is an internship project. The purpose for creating
this app was personal development in terms of programming 
(learn relevant tools, programming good practices and team communication). 
The project was created by two people.

# Technologies

Java → 11

PostgreSQL → 14.2

REST API

## Tools

Maven → 3.8.5

Git → 2.35.1

Swagger → 3.0.0

Liquibase → 4.10.0

## Frameworks

Spring Boot, Hibernate, Lombok, jUnit, Mockito, Slf4j

## IDE

IntelliJ IDEA, PgAdmin, Insomnia REST

# Setup

Info about the setup of the app is included in Setup Guide file (.pdf, .odt)

### tl;dr
Create two databases "bookshelf" and "bookshelfuser" with postgreSQL.
Change login and password with your postgreSQL credentials in both application.yml 
files. Location of the .yml files: 

src/main/resources/application.yml - main application

shelfuser/src/main/resources/application.yml - feign client

# Use of the app

Application is operative via Swagger UI.
After running the ShelfUserApplication (feign client) and BookshelfApplication (main app) classes, open this link in browser:
http://localhost:8467/swagger-ui/index.html#/ to get access to endpoints.