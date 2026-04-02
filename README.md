# School Portal Case Management System

This project is a high-security case management system designed for school environments. It focuses on managing assignment lifecycles, implementing contextual Role-Based Access Control (RBAC), handling secure file uploads to AWS S3, and maintaining detailed activity logs.

## Key Features

*   **UUID Primary Keys**: All primary keys across entities, repositories, services, and controllers have been migrated to UUIDs for enhanced security and scalability.
*   **Role-Based Access Control (RBAC)**: Implements both base and contextual roles to ensure granular access permissions.
*   **Secure File Handling**: Integrates with AWS S3 for storing files, providing pre-signed URLs for secure uploads and downloads.
*   **Auditing and Soft Deletes**: The `BaseEntity` includes fields for `createdBy`, `updatedBy`, and `deleted` status, with `AuditorAware` functionality to track user actions and soft-delete capabilities.
*   **Custom Exception Handling**: A `GlobalExceptionHandler` and custom exception classes manage application errors gracefully.
*   **Data Seeding**: Includes a `DataSeeder` utility (active in the `dev` profile) for populating the database with sample data.
*   **Layered DTOs**: Utilizes distinct DTOs (`CourseSurfaceResponse`, `CourseDetailResponse`, `AssignmentSurfaceResponse`, `AssignmentDetailResponse`) to control data visibility and complexity.
*   **Submission Entity**: A dedicated `Submission` entity allows for managing multiple drafts or versions of assignments.

## Technologies Used

*   **Language**: Java
*   **Framework**: Spring Boot
*   **Build Tool**: Maven
*   **Database**: PostgreSQL (Dockerized)
*   **Storage**: AWS S3
*   **Containerization**: Docker

## Current Status

The project has completed Phase 1 of its roadmap, focusing on database refinement, auditing, exception handling, and UUID migration. Phase 2 is currently in progress, with work on `SchoolClass` DTOs and visibility logic, as well as refinement of `AssignmentController` visibility, underway.

## Roadmap & Backlog

**Roadmap:**
### Phase 1: Database Refinement & Testing (Completed)
- Refine `BaseEntity` with auditing and soft-delete.
- Implement `AuditorAware`.
- Create `DatabaseIntegrationTest`.
- Implement `GlobalExceptionHandler` and custom exceptions.
- Add `DataSeeder`.
- Refactor `Course` entity.
- Introduce `Submission` entity.
- Complete UUID Migration.

### Phase 2: Layered DTOs & Visibility (In Progress)
- Create `SchoolClassSurfaceResponse` and `SchoolClassDetailResponse` DTOs.
- Update `DtoMapper.java` for school class DTOs.
- Refactor `SchoolClassController.java` for layered DTOs and visibility.
- Review and potentially implement layered visibility for `AssignmentController`.

**Backlog:**
- Multi-tenancy support
- Advanced reporting and analytics dashboard
- Mobile app integration / PWA features
- Plagiarism detection integration
- Automated grading/feedback workflows

## Future Proofing Ideas
- **Multi-Tenancy**: Planning for a `School` or `Organization` entity.
- **Event-Driven Architecture**: Consider using Spring Events for decoupling.
