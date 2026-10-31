# MediFlow - Hospital Management System

MediFlow is a backend-driven hospital management platform designed to streamline healthcare operations through secure, role-based workflows for patients, doctors, and administrators.

## Key Features

- JWT-based authentication and authorization.
- Role-Based Access Control (RBAC) for Patients, Doctors, and Administrators.
- Patient registration and profile management.
- Doctor directory and appointment management.
- Medical record creation and retrieval.
- Resource-level authorization and patient data isolation.
- Concurrency-safe appointment booking using pessimistic database locking.
- Duplicate appointment prevention with appropriate HTTP conflict responses.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend development |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Hibernate / Spring Data JPA | ORM and database access |
| MySQL 8.4 | Relational database |
| H2 | Automated testing |
| JUnit 5 & Mockito | Unit and integration testing |
| Maven | Build and dependency management |
| Docker & Docker Compose | Containerization |

## Architecture

The application follows a layered backend architecture:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Implements business logic and workflows.
- **Repository Layer:** Handles database operations.
- **Entity Layer:** Represents relational database models.
- **DTO Layer:** Controls request and response data.
- **Security Layer:** Handles JWT authentication and role-based access.

## Running Locally

### Prerequisites

- Java 21
- Docker Desktop
- Git

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Shivansh13-chak/MediFlow.git
   cd MediFlow
   ```

2. Create your local environment file:

   ```bash
   cp .env.example .env
   ```

3. Edit `.env` and provide your own database credentials and a secure JWT secret.

4. Build the application:

   ```bash
   docker build -t hospitalms:local .
   ```

5. Start the application and database:

   ```bash
   docker compose up -d
   ```

6. Access the backend at:

   `http://localhost:8080`

## Testing

Run the automated test suite using:

```bash
.\mvnw.cmd test
```

## Security

- Passwords are stored using BCrypt hashing.
- JWT authentication protects secured endpoints.
- Role-based authorization restricts access to protected resources.
- Resource-level checks help prevent unauthorized access to patient and doctor data.
- Appointment booking uses database locking to prevent duplicate concurrent bookings.

## Author

**Shivansh Chakravorty**

Backend Development Project | Java & Spring Boot