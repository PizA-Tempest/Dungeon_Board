# Tech Stack — Dungeon Board

## Backend

**Core:**
- Java 17+
- Spring Boot 3.2.x
- Maven (build tool)

**Dependencies:**
- Spring Web (REST API)
- Spring WebSocket (real-time)
- Spring Security (authentication)
- Spring Data JPA (database)
- H2 Database (development)
- PostgreSQL (production)
- JWT (jjwt library)
- Lombok (reduce boilerplate)

**Testing:**
- JUnit 5
- Mockito
- Spring Boot Test

## Frontend

**Core:**
- Vue.js 3.x (recommended) OR React 18+
- Vite 5.x (build tool)
- TypeScript (optional but recommended)

**UI & Styling:**
- Tailwind CSS 3.x
- Headless UI or similar component library
- VueUse (React equivalent: React Use)

**State & Data:**
- Pinia (Vue) OR Zustand/React Context (React)
- Axios (HTTP client)
- Native WebSocket or Stomp.js

**Development:**
- ESLint
- Prettier
- Vite Plugin ESLint

## DevOps

**Version Control:**
- Git
- GitHub (recommended for hosting)

**Development:**
- Hot module reload (Vite)
- Spring Boot DevTools (backend)

**Future (not MVP):**
- Docker (containerization)
- CI/CD pipeline
- Cloud deployment (AWS/Heroku/Azure)

## Development Environment

**Required:**
- JDK 17 or higher
- Node.js 18+ or 20+
- npm or yarn
- Maven 3.8+
- IDE (IntelliJ IDEA recommended for Java, VS Code for frontend)

**Port Configuration:**
- Backend: `localhost:8080`
- Frontend: `localhost:5173` (Vite default)

---

This document is part of the Memory Bank.
Update when technologies are added or changed.
