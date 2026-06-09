# Step-By-Step Learning Guide

This guide explains the project from the outside in. Keep the backend and frontend folders open while reading it.

## 1. Create The Two App Parts

The project is split into two separate apps:

```text
backend   -> Spring Boot API and database
frontend  -> React browser app
```

This is common in full stack projects. The backend owns data and rules. The frontend owns the screen and user interaction.

## 2. Backend Dependencies

The backend dependencies live in `backend/pom.xml`.

Important dependencies:

- `spring-boot-starter-web`: creates REST APIs.
- `spring-boot-starter-data-jpa`: talks to the database using Java classes.
- `spring-boot-starter-validation`: validates incoming JSON.
- `h2`: gives us a simple in-memory database.
- `spring-boot-starter-test`: gives testing tools.

## 3. Backend Configuration

The backend settings live in:

```text
backend/src/main/resources/application.properties
```

Important settings:

- `server.port=8080`: backend runs on port 8080.
- `spring.datasource.url=jdbc:h2:mem:urlshortener`: creates an in-memory H2 database.
- `spring.jpa.hibernate.ddl-auto=update`: lets JPA create/update tables.
- `app.frontend-origin=http://localhost:5173`: allows React to call the API during development.
- `app.backend-base-url=http://localhost:8080`: used to build the final short URL.

## 4. Application Entry Point

File:

```text
backend/src/main/java/com/example/urlshortener/UrlShortenerApplication.java
```

This class starts Spring Boot. The `@SpringBootApplication` annotation tells Spring to scan this package, find controllers/services/repositories, and wire them together.

## 5. CORS Setup

File:

```text
backend/src/main/java/com/example/urlshortener/config/WebConfig.java
```

React runs at `localhost:5173`, while Spring Boot runs at `localhost:8080`. Browsers block cross-origin API calls unless the backend allows them.

`WebConfig` allows requests from the React origin to `/api/**`.

## 6. Entity: Database Shape

File:

```text
backend/src/main/java/com/example/urlshortener/url/UrlMapping.java
```

An entity is a Java class that maps to a database table.

This app stores:

- `id`: database primary key.
- `originalUrl`: the long URL.
- `shortCode`: the generated short code.
- `visitCount`: how many redirects happened.
- `createdAt`: when the row was created.

The `@PrePersist` method runs before a new row is saved, so `createdAt` is filled automatically.

## 7. Repository: Database Access

File:

```text
backend/src/main/java/com/example/urlshortener/url/UrlMappingRepository.java
```

The repository extends `JpaRepository`, which gives basic methods like:

- `save`
- `findAll`
- `findById`
- `deleteById`

The custom method names are understood by Spring Data JPA:

- `findByShortCode`
- `existsByShortCode`
- `findTop20ByOrderByCreatedAtDesc`

Spring turns those method names into database queries.

## 8. DTOs: API Input And Output

Files:

```text
backend/src/main/java/com/example/urlshortener/url/CreateShortUrlRequest.java
backend/src/main/java/com/example/urlshortener/url/UrlMappingResponse.java
```

DTO means Data Transfer Object.

`CreateShortUrlRequest` represents the JSON React sends:

```json
{
  "originalUrl": "https://example.com"
}
```

`UrlMappingResponse` represents the JSON Spring Boot sends back:

```json
{
  "id": 1,
  "originalUrl": "https://example.com",
  "shortCode": "aB12Cd3",
  "shortUrl": "http://localhost:8080/aB12Cd3",
  "visitCount": 0,
  "createdAt": "2026-06-09T10:00:00Z"
}
```

## 9. Service: Business Logic

File:

```text
backend/src/main/java/com/example/urlshortener/url/UrlShortenerService.java
```

The service contains the core logic:

1. Generate a random 7-character code.
2. Check that the code is not already used.
3. Save the original URL and code.
4. Find recent URLs.
5. Look up a code during redirect.
6. Increase the visit count.

Business logic belongs here because controllers should stay focused on HTTP requests and responses.

## 10. Controller: HTTP Endpoints

File:

```text
backend/src/main/java/com/example/urlshortener/url/UrlController.java
```

The controller exposes three important routes:

```text
POST /api/urls
GET  /api/urls
GET  /{shortCode}
```

`POST /api/urls` creates a short URL.

`GET /api/urls` returns the 20 newest URLs.

`GET /{shortCode}` redirects the browser to the original URL.

The controller also handles validation errors and missing short codes.

## 11. Backend Tests

File:

```text
backend/src/test/java/com/example/urlshortener/url/UrlControllerTest.java
```

The tests use `MockMvc` to call the API without opening a real browser.

The two tests check:

- A valid URL creates a short URL.
- An invalid URL returns a bad request.

Run tests with:

```bash
cd backend
mvn test
```

## 12. Frontend Dependencies

The frontend dependencies live in:

```text
frontend/package.json
```

Important dependencies:

- `react`: builds the UI.
- `react-dom`: renders React into the page.
- `vite`: runs and builds the frontend.
- `lucide-react`: gives clean UI icons.

## 13. React Entry Point

File:

```text
frontend/src/main.jsx
```

This file renders `<App />` into the HTML element with `id="root"`.

That root element lives in:

```text
frontend/index.html
```

## 14. API Helper

File:

```text
frontend/src/api.js
```

This file keeps fetch logic in one place.

`createShortUrl(originalUrl)` sends:

```text
POST http://localhost:8080/api/urls
```

`getRecentUrls()` sends:

```text
GET http://localhost:8080/api/urls
```

If the backend returns an error, the helper throws a JavaScript `Error`, which the React component can display.

## 15. React App Component

File:

```text
frontend/src/App.jsx
```

Important state variables:

- `originalUrl`: what the user typed.
- `urls`: the recent URL list.
- `latestUrl`: the most recently created short URL.
- `status`: tracks whether the form is saving.
- `error`: stores an error message.
- `copiedCode`: remembers which URL was copied.

Important functions:

- `loadRecentUrls`: fetches saved URLs from the backend.
- `handleSubmit`: sends the long URL to the backend.
- `copyToClipboard`: copies the short URL.

The `useEffect` call loads recent URLs when the page first opens.

## 16. Styling

File:

```text
frontend/src/App.css
```

The CSS controls layout, spacing, colors, responsive behavior, and button states.

The page uses:

- A centered workspace.
- A form panel.
- A recent URL list.
- Responsive layout rules for small screens.

## 17. Run The Whole App

Terminal 1:

```bash
cd backend
mvn spring-boot:run
```

Terminal 2:

```bash
cd frontend
npm run dev
```

Open:

```text
http://localhost:5173
```

## 18. Test The Flow

1. Paste a URL like `https://spring.io/projects/spring-boot`.
2. Click `Shorten`.
3. Copy or open the generated short URL.
4. Refresh the recent list.
5. The visit count should increase after redirects.

## 19. Ideas To Practice Next

- Add delete buttons for URLs.
- Add custom aliases so users can choose their own short code.
- Replace H2 with MySQL or PostgreSQL.
- Add user login.
- Add expiration dates for short links.
- Add analytics by date.
