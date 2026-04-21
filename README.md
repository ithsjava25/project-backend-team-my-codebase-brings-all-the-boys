# README - School Portal Case Management System

This project is a high-security case management system designed for school environments. It focuses on managing assignment lifecycles, implementing contextual Role-Based Access Control (RBAC), handling secure file uploads to AWS S3, and maintaining detailed activity logs.

## Core Features
- **Assignment Lifecycle**: Creation, assignment, communication, submission, and evaluation.
- **Strict RBAC**: Roles including Admin, Teacher, Student, Staff, and Guest.
- **Activity Logging**: Automated tracking of all major system events.
- **File Management**: S3-compatible storage integration.
- **Interactive Communication**: Commenting system for feedback.
- **Authentication**: Session-based auth + GitHub OAuth2.

## Technology Stack
- **Backend**: Java 25, Spring Boot 4.0.4
- **Database**: PostgreSQL (Dockerized)
- **Frontend**: React 19.2.4, Vite 8.0.1, Tailwind CSS, Shadcn UI

## Roadmap

### Phase 1-5: Foundation to Course Management [Completed]
- Core entity models, security, S3 integration, and school portal management logic are in place.

### Phase 6-7: Contextual Security & Advanced Frontend [Completed]
- Contextual RBAC for controllers, refined DTO mapping, and a modern React dashboard with role-based access.

## Current Todo List
- [ ] Implement Scoped/Contextual Security checks in Controllers.
- [ ] Develop comprehensive Edit features for Courses, Assignments, and other entities.
- [ ] Refine list views across the application for better data display and UX.
- [ ] Fix Activity Log display in the dashboard.
- [ ] Fix Activity Log sidebar navigation.
- [ ] Update Dashboard overview (resolve repeated entity issues).
- [ ] Implement User Profile view (clickable from user list).
- [ ] Debug and fix data integrity issues for Courses (resolving 500 errors).
- [ ] Update data seeding to include end dates for courses.

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
