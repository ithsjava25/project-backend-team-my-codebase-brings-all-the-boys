# README - School Portal Case Management System

This project is a high-security case management system designed for school environments. It focuses on managing assignment lifecycles, implementing contextual Role-Based Access Control (RBAC), handling secure file uploads to AWS S3, and maintaining detailed activity logs.

## Core Features
- **Assignment Lifecycle**: Creation, assignment, communication, submission, and evaluation.
- **Strict Scoped RBAC**: Contextual permissions (Mentor vs Teacher vs Student) with enrollment-based access control.
- **School Class Management**: Full CRUD for school classes and viewing associated courses/participants.
- **Activity Logging**: Automated tracking of all major system events with descriptive details and robust null-safety.
- **File Management**: S3-compatible storage integration with pre-signed URL security.
- **User Profiles**: Comprehensive views of user info, including enrolled classes and courses.
- **Authentication**: Session-based auth + GitHub OAuth2.

## Technology Stack
- **Backend**: Java 25, Spring Boot 4.0.5
- **Database**: PostgreSQL (Dockerized)
- **Frontend**: React 19.2.4, Vite 8.0.1, Tailwind CSS, Shadcn UI
- **Testing**: JUnit 5, Mockito (131 tests passing)

## Roadmap

### Phase 1-5: Foundation to Course Management [Completed]
- Core entity models, security, S3 integration, and school portal management logic are in place.

### Phase 6-7: Contextual Security & Advanced Frontend [Completed]
- Contextual RBAC for controllers, refined DTO mapping, and a modern React dashboard with role-based access.
- Implemented comprehensive Edit/Delete features for Courses, Assignments, and School Classes.
- Fixed 500 errors related to lazy loading and missing cascade types.

## Current Todo List
- [ ] Debug S3 integration issues (e.g., file uploads failing with CORS error / 403).
- [ ] Implement soft deadlines (allow late with "Late" flag).
- [ ] Auto-status updates via @Scheduled tasks.
- [ ] Automatic course archiving/read-only mode after endDate.

## Getting Started

### Prerequisites
- Docker
- Java 25+
- Maven
- Node.js 20+

### Backend Setup
1. **Start Database**: `docker compose up -d`
2. **Configure**: Update `application-dev.properties` with GitHub OAuth2 credentials.
3. **Run**: `./mvnw spring-boot:run`

### Frontend Setup
1. `cd frontend`
2. `npm install`
3. `npm run dev`

---
*Maintained with Gemini CLI*
