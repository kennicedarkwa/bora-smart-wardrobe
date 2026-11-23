# BORA – Smart Wardrobe

Web app for managing a digital wardrobe. Users store their clothes and get outfit suggestions based on the current weather.

Team project in the course Software Engineering & Architecture 2 at HAW Hamburg (7 students, agile).

## My role: Testing & QA

- Unit and integration tests with **JUnit 5**, **Mockito** and **MockMvc**
- API testing of the REST endpoints with **Postman**
- Test documentation, updated with each iteration
- Responsive layout fixes (CSS media queries)

## Tech stack

- **Backend:** Java, Spring Boot, Gradle
- **Database:** MariaDB
- **API:** REST
- **Frontend:** HTML, CSS, JavaScript

## Running the app

Requirements: Java and a database configured in `src/main/resources/application.properties`.

    ./gradlew bootRun

Stop with `Ctrl + C`.

## Running the tests

    ./gradlew test
