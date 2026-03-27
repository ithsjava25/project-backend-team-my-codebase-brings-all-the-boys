# School Portal Case Management System

## Project Overview
This project is a modern, high-security case management system designed for school environments. It enables teachers to manage assignments (cases) with a full lifecycle, while ensuring strict confidentiality and role-based access control (RBAC). The system features file uploads to S3-compatible storage and automated activity logging for full transparency.

## Core Features
- **Assignment Lifecycle**: Creation, assignment, communication, submission, and evaluation.
- **Strict RBAC**: Roles including Admin, Teacher, Student, Staff, and Guest.
- **Confidentiality**: Users (students) only have access to their own assignments and related data.
- **Activity Logging**: Automated tracking of all major events (role changes, assignments, comments).
- **File Management**: Metadata stored in PostgreSQL, with actual files residing in S3-compatible storage.
- **Interactive Communication**: Commenting system for ongoing feedback on assignments.

## Technology Stack
- **Backend**: Java 25, Spring Boot 4.0.4
- **Database**: PostgreSQL (Dockerized)
- **Security**: Spring Security (OAuth2/JWT preparation)
- **File Storage**: S3-compatible (Integration in progress)
- **Frontend**: React (TypeScript)
- **Persistence**: Spring Data JPA / Hibernate

## Project Structure
```text
src/main/java/org/example/projectbackendteammycodebasebringsalltheboys/
├── config      # Security, S3, and OpenAPI configurations
├── controller  # REST endpoints
├── dto         # Data Transfer Objects
├── entity      # JPA Database Entities
├── enums       # System-wide Enums (Status, Roles)
├── exception   # Global error handling
├── mapper      # Entity-DTO mapping logic
├── repository  # Spring Data JPA Repositories
├── security    # Authentication and Authorization logic
├── service     # Business logic and orchestration
└── storage     # S3 integration services
```

## Current State & Progress
The project has successfully completed Phase 1 (Foundation) and Phase 2 (Core Logic).
- [x] Dockerized PostgreSQL environment set up.
- [x] Professional project structure implemented.
- [x] Core Entities and Enums defined.
- [x] JPA Auditing for automatic timestamping enabled.
- [x] All Repositories with custom query methods implemented.
- [x] Comprehensive DTO layer for API communication.
- [x] Full Service Layer (Case, User, Comment, File, Activity Logging, Authorization).
- [x] `LocalStorageService` implemented for development file handling.

## Roadmap & Backlog
### Phase 2: Core Logic & Security (Current)
- [ ] Implement Spring Security configuration (RBAC & JWT).
- [ ] Create User and Auth services for login/registration.
- [ ] Implement Case management business logic.
- [ ] Set up DTOs and Mappers for clean API communication.

### Phase 3: S3 Integration & File Handling
- [ ] Integrate AWS SDK for Java.
- [ ] Implement secure file upload and download services.
- [ ] Link file metadata to assignments and comments.

### Phase 4: Communication & Auditing
- [ ] Build the commenting engine.
- [ ] Implement the `ActivityLogService` for automated event tracking.
- [ ] Develop real-time update notifications.

### Phase 5: Frontend (JTE)
- [ ] Create role-specific dashboards.
- [ ] Build forms for assignment creation and submission.
- [ ] Implement a history view for case owners.

## Getting Started
1. **Prerequisites**: Docker, Java 25+, Maven.
2. **Database**: Run `docker compose up -d` to start the PostgreSQL instance.
3. **Run**: Execute `./mvnw spring-boot:run` to start the application.
4. **Access**: The database is available at `localhost:5432` with user `admin` and password `admin`.
