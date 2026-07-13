# Job Application Tracker

A full-stack web application to track job applications, deadlines, and interview progress.

## Tech Stack

**Backend**
- Java 21+ / Spring Boot 3.x
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL with Flyway migrations
- JUnit 5 + Mockito

**Frontend** (Phase 2)
- React 18 with Vite
- TailwindCSS
- React Router

**Infrastructure**
- Docker + Docker Compose
- GitHub Actions (CI)
- Render (backend hosting)
- Vercel (frontend hosting)

## Status

🚧 **In development** — Phase 1: Backend foundation

## Getting Started

### Prerequisites
- Java 21+
- Docker Desktop
- Git

### Setup

Clone the repository:
\`\`\`
git clone https://github.com/Dhaval1512/job-application-tracker.git
cd job-application-tracker
\`\`\`

Start PostgreSQL:
\`\`\`
docker compose up -d
\`\`\`

Run the backend:
\`\`\`
cd Backend
./mvnw spring-boot:run
\`\`\`

The API will be available at `http://localhost:8080`.

### Verify

\`\`\`
curl http://localhost:8080/api/health
\`\`\`

Expected response:
\`\`\`json
{"status":"ok","service":"jobtracker","timestamp":"..."}
\`\`\`

### Run Tests

\`\`\`
cd Backend
./mvnw test
\`\`\`

## Roadmap

- [ ] Phase 1: Core backend, auth, deployed API
- [ ] Phase 2: React frontend, end-to-end integration
- [ ] Phase 3: Analytics dashboard, reminders, differentiators

## Author

Dhaval Patel
