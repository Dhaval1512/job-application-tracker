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
