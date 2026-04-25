# README - School Portal Case Management System

This project is a high-security case management system designed for school environments. It focuses on managing assignment lifecycles, implementing contextual Role-Based Access Control (RBAC), handling secure file uploads to AWS S3, and maintaining detailed activity logs.

## Core Features
- **Assignment Lifecycle**: Creation, assignment, communication, submission, and evaluation. Includes a "Turn In" feature for students.
- **Strict Scoped RBAC**: Contextual permissions (Mentor vs Teacher vs Student) with enrollment-based access control.
- **Bi-directional Management**: Admins can manage student enrollments from both the User Edit and School Class Edit views.
- **Activity Logging**: Automated tracking of system events with advanced filtering (User, Action, Entity Type, Status).
- **File Management**: S3-compatible storage integration with student-specific file persistence in assignments. 
- **User Profiles**: Detailed profiles for all users with self-editing capabilities for personal credentials.
- **Evaluation System**: Teachers can review and revise grades for completed assessments.

## Technology Stack
- **Backend**: Java 21, Spring Boot 4.0.5
- **Database**: PostgreSQL (Dockerized)
- **Frontend**: React 19.2.4, Vite 8.0.1, Tailwind CSS, Shadcn UI
- **Testing**: JUnit 5, Mockito, Vitest for frontend

## Roadmap

### Phase 1-5: Foundation to Course Management [Completed]
- Core entity models, security, S3 integration, and school portal management logic are in place.

### Phase 6-7: Contextual Security & Advanced Frontend [Completed]
- Implemented comprehensive Edit/Delete features for Courses, Assignments, and School Classes.
- Optimized performance with eager fetching (EntityGraph) and EXISTS subqueries to prevent N+1 issues and Cartesian products.
- Hardened User Profiles with scoped authorization and shared course/class visibility.
- Developed self-edit functionality for user profiles with current password verification.
- Resolved Hibernate 6 soft-delete incompatibilities and DataIntegrityViolation issues in seeding.
- Added advanced Activity Log filtering and fixed deterministic paging.
- Implemented students' "Turn In" workflow and improved status visibility.

## Current Todo List
- [ ] Debug S3 integration issues (e.g., file uploads failing with CORS error / 403).
- [ ] Implement soft deadlines (allow late with "Late" flag).
- [ ] Auto-status updates via @Scheduled tasks.
- [ ] Automatic course archiving/read-only mode after endDate.
- [ ] Orphaned Course survival strategy (Investigate 404s after class deletion).

## Getting Started

### Prerequisites
- Docker
- Java 21+
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
