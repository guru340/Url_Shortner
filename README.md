# Full Stack URL Shortener

This project is a learning-friendly full stack app built with:

- Spring Boot backend
- React frontend
- H2 in-memory database
- REST APIs

The app lets a user paste a long URL, creates a short code, lists recently shortened URLs, and redirects visitors from the short code to the original URL.

## Project Structure

```text
Url Shortner/
  backend/   Spring Boot REST API
  frontend/  React user interface
```

## How The App Works

1. The React form sends a long URL to the Spring Boot API.
2. The backend validates the URL and stores it in the H2 database.
3. The backend creates a short code like `aB93xQ`.
4. React displays the short URL to the user.
5. When someone opens the generated short URL, Spring Boot looks up the code and redirects to the original URL.

## Backend Setup

Open a terminal in the `backend` folder:

```bash
mvn spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

Useful backend URLs:

- `POST http://localhost:8080/api/urls` creates a short URL
- `GET http://localhost:8080/api/urls` lists all stored URLs
- `GET http://localhost:8080/{shortCode}` redirects to the long URL
- `GET http://localhost:8080/h2-console` opens the H2 database console

H2 login:

```text
JDBC URL: jdbc:h2:mem:urlshortener
User: sa
Password:
```

## Frontend Setup

Open a second terminal in the `frontend` folder:

```bash
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

## Learning Path

Read the code in this order:

1. `backend/src/main/java/com/example/urlshortener/UrlShortenerApplication.java`
2. `backend/src/main/java/com/example/urlshortener/url/UrlMapping.java`
3. `backend/src/main/java/com/example/urlshortener/url/UrlMappingRepository.java`
4. `backend/src/main/java/com/example/urlshortener/url/UrlShortenerService.java`
5. `backend/src/main/java/com/example/urlshortener/url/UrlController.java`
6. `frontend/src/api.js`
7. `frontend/src/App.jsx`
8. `frontend/src/App.css`

## Why These Pieces Exist

- Entity: describes a database table as a Java class.
- Repository: gives simple database operations without writing SQL.
- Service: holds business logic, like creating short codes.
- Controller: exposes HTTP endpoints.
- DTO: controls what data enters and leaves the API.
- React component: renders the UI and calls the API.

## Common Commands

Run backend tests:

```bash
cd backend
mvn test
```

Build frontend:

```bash
cd frontend
npm run build
```

## Run With Docker Compose

From the project root:

```bash
docker compose up --build
```

Open the frontend:

```text
http://localhost:3000
```

The backend runs at:

```text
http://localhost:8080
```

Stop containers:

```bash
docker compose down
```

## Deployment Settings

Set this on the deployed backend:

```text
FRONTEND_ORIGIN=https://your-vercel-frontend-url
```

Set this on the deployed frontend:

```text
VITE_API_BASE_URL=https://your-render-backend-url
```

The backend now creates short URLs from the real request domain. For example, when the API is called at Render, the generated short URL will look like:

```text
https://your-render-backend-url/abc1234
```
---

<p align="center">
  Built  by <b>Mayank Sangwani</b>
</p>
