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
- **Authentication**: Session-based authentication with GitHub OAuth2 integration.

### Advanced Architecture Features
- **UUID Primary Keys**: All primary keys across entities, repositories, services, and controllers use UUIDs for enhanced security and scalability
- **Auditing & Soft Deletes**: `BaseEntity` includes `createdBy`, `updatedBy`, and `deleted` status with `AuditorAware` functionality
- **Data Seeding**: `DataSeeder` utility (active in `dev` profile) for populating database with sample data
- **Layered DTOs**: Distinct DTOs (`CourseSurfaceResponse`, `CourseDetailResponse`, `AssignmentSurfaceResponse`, `AssignmentDetailResponse`) control data visibility
- **Submission Entity**: Dedicated entity for managing multiple drafts/versions of assignments

## Technology Stack
- **Backend**: Java 25, Spring Boot 4.0.4
- **Database**: PostgreSQL (Dockerized)
- **Security**: Spring Security (Session-based auth + OAuth2 for GitHub login)
- **File Storage**: S3-compatible
- **Frontend**: React 19.2.4, Vite 8.0.1, React Router 7.13.2, Axios 1.14.0
- **Persistence**: Spring Data JPA / Hibernate

## Project Structure
```text
src/main/java/org/example/projectbackendteammycodebasebringsalltheboys/
├── config      # Security, CORS, S3, and OpenAPI configurations
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

### Frontend Project Structure
```text
frontend/
├── src/
│   ├── api/
│   │   └── client.js           # Axios instance with base URL and credentials
│   ├── components/
│   │   └── ProtectedRoute.jsx  # Route guard for authenticated pages
│   ├── context/
│   │   └── AuthContext.jsx     # Authentication context provider
│   ├── hooks/
│   │   └── useAuth.js          # Custom hook for auth state management
│   ├── pages/
│   │   ├── Dashboard.jsx       # Protected dashboard page
│   │   └── LoginPage.jsx       # Login/Register page with GitHub OAuth
│   ├── App.jsx                 # Root component with routing
│   └── main.jsx                # Application entry point
├── vite.config.js              # Vite configuration with API proxy
└── package.json                # Frontend dependencies
```

## How It Works - Authentication Flow

### Username/Password Authentication
1. User enters credentials on `/login` page
2. Frontend sends `POST /api/auth/login` to backend
3. Backend validates credentials and creates session
4. Session cookie is automatically stored in browser
5. Frontend fetches user data via `GET /api/auth/me`
6. User is redirected to `/dashboard`

### GitHub OAuth2 Authentication
1. User clicks "Login using GitHub" on `/login` page
2. Frontend redirects to `http://localhost:8080/oauth2/authorization/github`
3. GitHub asks user to authorize the application
4. On success, GitHub redirects to `/login/oauth2/code/github`
5. Backend creates session and redirects to `http://localhost:5173/dashboard`
6. Frontend fetches user data via `GET /api/auth/me`

### Session Management
- **Session Storage**: Backend uses Spring Security 6 with session-based authentication
- **Cookie Handling**: Axios is configured with `withCredentials: true` to send session cookies with every request
- **Auth State**: Frontend uses React Context (`AuthContext`) to manage authentication state globally
- **Auto-Login**: On page load, frontend automatically fetches current user via `/api/auth/me`
- **Protected Routes**: `ProtectedRoute` component checks auth state before allowing access

### API Communication
- **Base URL**: Frontend uses `/api` as base URL (proxied to `http://localhost:8080` by Vite)
- **Credentials**: All requests include session cookies automatically
- **Error Handling**: Frontend displays user-friendly error messages from backend responses

## Current State & Progress
The project has successfully completed Phase 1 (Foundation), Phase 2 (Core Logic), and Phase 3 (Frontend Integration).

### ✅ Phase 3: Frontend Integration
- [x] React 19.2.4 with Vite 8.0.1 setup and configuration
- [x] React Router 7.13.2 for client-side routing
- [x] Axios client with session cookie handling (`withCredentials: true`)
- [x] Vite dev server proxy configuration for API calls
- [x] Authentication context provider (`AuthContext`) for global auth state
- [x] Custom `useAuth` hook for authentication logic
- [x] `ProtectedRoute` component for route guards
- [x] Login page with username/password authentication
- [x] Registration page with email confirmation
- [x] Login/Register toggle functionality
- [x] GitHub OAuth2 integration with redirect to dashboard
- [x] Dashboard page displaying user information
- [x] Logout functionality with session cleanup
- [-] Error handling and user-friendly error messages (needs more error handling)
- [x] Automatic user fetching on app load
- [x] Auto-redirect to login for unauthenticated users
- [x] Auto-redirect to dashboard for authenticated users

### Foundation Infrastructure
- [x] Dockerized PostgreSQL environment set up
- [x] Professional project structure implemented
- [x] Core Entities and Enums defined
- [x] Core Entities and Enums defined.
- [x] JPA Auditing for automatic timestamping enabled.
- [x] All Repositories with custom query methods implemented.
- [x] Comprehensive DTO layer for API communication.
- [x] Full Service Layer (Case, User, Comment, File, Activity Logging, Authorization).
- [x] `LocalStorageService` implemented for development file handling.
- [x] Spring Security configuration with OAuth2 and session-based authentication.
- [x] REST API endpoints for user registration, login, logout, and current user.
- [x] CORS configuration for React frontend integration.
- [x] UUID migration across all entities, repositories, services, and controllers
- [x] `BaseEntity` with auditing (createdBy, updatedBy) and soft-delete (deleted) support
- [x] `AuditorAware` implementation for automatic user tracking
- [x] `DataSeeder` utility for dev environment sample data
- [x] Layered DTOs for controlled data visibility (Surface vs Detail responses)
- [x] `Submission` entity for assignment version management
- [x] `GlobalExceptionHandler` with custom exception classes
- [x] `DatabaseIntegrationTest` for database validation
- [x] OAuth2 GitHub login integration.

## Roadmap & Backlog

### ✅ Phase 2: Core Logic & Security
- [x] Implement Spring Security configuration (Session-based auth + OAuth2).
- [x] Create User and Auth services for login/registration.
- [x] Implement Case management business logic.
- [x] Set up DTOs and Mappers for clean API communication.
- [x] Configure CORS for React frontend.

### ✅ Phase 3: Frontend Integration
- [x] React 19.2.4 + Vite 8.0.1 setup with React Router 7.13.2
- [x] Axios client with session cookie handling (`withCredentials: true`)
- [x] Vite dev server proxy configuration for API calls
- [x] Authentication context provider (`AuthContext`) for global auth state
- [x] Custom `useAuth` hook for authentication logic
- [x] `ProtectedRoute` component for route guards
- [x] Login page with username/password authentication
- [x] Registration page with email confirmation
- [x] GitHub OAuth2 integration with redirect to dashboard
- [x] Dashboard page displaying user information
- [x] Logout functionality with session cleanup
- [x] REST API for authentication (`/api/auth/register`, `/api/auth/login`, `/api/auth/logout`, `/api/auth/me`)
- [x] Session management with Spring Security 6
- [x] CORS configuration for development environment

### 🔄 Phase 4: S3 Integration & File Handling
- [x] AWS SDK for Java integration
- [x] Secure file upload/download with S3Client
- [x] Pre-signed URLs for secure access (15 min expiry)
- [x] File metadata linked to assignments via File entity
- [ ] Configure real AWS credentials in production

### 📋 Phase 5: Communication & Auditing
- [ ] Build the commenting engine.
- [ ] Implement the `ActivityLogService` for automated event tracking.
- [ ] Develop real-time update notifications.

### 📋 Phase 6: Frontend (React) - Additional Features (Planned)
- [ ] Create role-specific dashboards.
- [ ] Build forms for assignment creation and submission.
- [ ] Implement a history view for case owners.

## Future Proofing Ideas
- **Multi-Tenancy**: Planning for `School` or `Organization` entity for multi-school support
- **Event-Driven Architecture**: Consider using Spring Events for decoupling domain events
- **Advanced Analytics**: Reporting dashboard for assignment statistics and student performance
- **Plagiarism Detection**: Integration with plagiarism detection services
- **Automated Workflows**: AI-powered grading/feedback systems

## Getting Started

### Prerequisites
- Docker
- Java 25+
- Maven
- Node.js 20+ (for React frontend)

### Backend Setup

1. **Start Database**:
   ```bash
   docker compose up -d
   ```
   The PostgreSQL instance will be available at `localhost:5432`.

2. **Configure Application**:
   - The `dev` profile is active by default.
   - Edit `src/main/resources/application-dev.properties` to configure:
     - `frontend.url=http://localhost:5173` (React dev server)
     - Database credentials (default: `admin`/`admin`)
     - GitHub OAuth2 credentials

3. **GitHub OAuth2 Setup** (for local development):
   - Create a new OAuth App in GitHub Settings → Developer settings → OAuth Apps
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
   - Add `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` to `application-dev.properties`

4. **Run Backend**:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

### Frontend Setup

1. **Navigate to Frontend**:
    ```bash
    cd frontend
    ```

2. **Install Dependencies**:
    ```bash
    npm install
    ```

3. **Start Dev Server**:
    ```bash
    npm run dev
    ```
    The React app will be available at `http://localhost:5173`.

**Note**: The Vite dev server proxies API requests to `http://localhost:8080` automatically. See `vite.config.js` for proxy configuration.

### API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login with username/password
- `GET /api/auth/me` - Get current user info
- `POST /api/auth/logout` - Logout

#### OAuth2
- `GET /oauth2/authorization/github` - Initiate GitHub login
- Callback: `/login/oauth2/code/github` → Redirects to `http://localhost:5173/dashboard`

### Configuration Profiles

- **dev** (default): Local development with PostgreSQL on localhost
- **test**: Integration tests with H2 in-memory database
- **prod**: Production environment (create `application-prod.properties` as needed)

## Database Access

- **URL**: `localhost:5432`
- **Database**: `schoolportal`
- **User**: `admin`
- **Password**: `admin`

## Security Notes

- CSRF is disabled for REST API (suitable for React + Session-based auth)
- CORS is restricted to configured `frontend.url`
- Session management uses Spring Security 6 with explicit save
- OAuth2 state parameter protects against CSRF attacks
