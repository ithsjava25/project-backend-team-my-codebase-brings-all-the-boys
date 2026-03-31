# School Portal Case Management System

## Project Overview
This project is a modern, high-security case management system designed for school environments. It enables teachers to manage assignments (cases) with a full lifecycle, while ensuring strict confidentiality and role-based access control (RBAC). The system features file uploads to S3-compatible storage and automated activity logging for full transparency.

## Core Features
- **Assignment Lifecycle**: Creation, assignment, communication, submission, and evaluation.
- **Contextual RBAC**: Roles including Admin, Teacher, Student, and Mentor. Users can have different roles in different classes (e.g., a Teacher can be a Mentor for one class and a regular Teacher for another).
- **Strict Confidentiality**: Users only have access to their own assignments, and those in classes where they are enrolled.
- **S3 File Management**: Secure file handling using **AWS SDK v2** with **Pre-signed URLs** for direct client-to-cloud transfers.
- **Activity Logging**: Automated tracking of all major events (role changes, assignments, comments).
- **Interactive Communication**: Commenting system for ongoing feedback on assignments.

## Technology Stack
- **Backend**: Java 25, Spring Boot 4.0.4
- **Database**: PostgreSQL (Dockerized)
- **Security**: Spring Security (OAuth2 / Session-based, JWT in progress)
- **File Storage**: S3-compatible (Pre-signed URL strategy)
- **Frontend**: React (Pure REST Backend)
- **Persistence**: Spring Data JPA / Hibernate

## Project Structure
```text
src/main/java/org/example/projectbackendteammycodebasebringsalltheboys/
├── config      # Security, S3, and Storage configurations
├── controller  # REST endpoints (API Prefix)
├── dto         # Data Transfer Objects (Case, Comment, File, User)
├── entity      # JPA Database Entities (Class, Course, Assignment, etc.)
├── enums       # System-wide Enums (Status, Roles, ClassRoles)
├── mapper      # Entity-DTO mapping logic (DtoMapper)
├── repository  # Spring Data JPA Repositories
├── security    # Authentication and Authorization logic
├── service     # Business logic and orchestration
└── storage     # S3 and Local storage implementations
```

## Current State & Progress (Updated 2026-03-31)
The project has established a robust foundation and moved into advanced feature sets.
- [x] **Infrastructure**: Dockerized PostgreSQL and S3 configuration.
- [x] **RBAC**: Base roles (ADMIN, TEACHER, STUDENT) and Contextual roles (MENTOR, ASSISTANT) implemented.
- [x] **REST API**: Completed `AssignmentController`, `CommentController`, and `FileController`.
- [x] **File Strategy**: Implemented AWS S3 integration with pre-signed upload/download URLs.
- [x] **Data Model**: Implemented `SchoolClass`, `Course`, and `ClassEnrollment` for future-proof scalability.
- [x] **Services**: Full service layer for Classes, Courses, Enrollments, Assignments, and Files.
- [x] **Stability**: Resolved build issues and upgraded to AWS SDK 2.42.23.

## Roadmap & Backlog
### Phase 6: Contextual Security (Next)
- [ ] Implement Scoped Security: Use `ClassEnrollment` to authorize API requests.
- [ ] Refactor existing controllers to enforce class-based boundaries.
- [ ] Implement "Public Profile" vs "Private Details" logic for unauthorized users.

### Phase 7: Communication & Real-time
- [ ] Finalize REST Controllers for course/class management.
- [ ] (Optional) Add real-time event notifications for new comments/assignments.

### Phase 8: Security Hardening
- [ ] Complete JWT-based Authentication for stateless REST communication.
- [ ] Implement Global Exception Handling for consistent API error responses.

## Getting Started
1. **Prerequisites**: Docker, Java 25+, Maven.
2. **Database**: Run `docker compose up -d` to start the PostgreSQL instance.
3. **Storage**: Configure `aws.s3.*` properties in `application.properties`. Toggle `storage.type=local` for local development.
4. **Run**: Execute `./mvnw spring-boot:run` to start the application.
5. **Access**: API endpoints are prefixed with `/api`.
