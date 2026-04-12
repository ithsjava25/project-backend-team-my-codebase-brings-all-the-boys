# Activity Logging

The activity logging system records significant user actions across the application using an AOP-based `@LogActivity` annotation. Logs are stored in the `activity_logs` table and exposed via a REST API.

---

## How It Works

1. A service method is annotated with `@LogActivity`.
2. After the method returns (or throws), `ActivityLoggingAspect` intercepts the call.
3. The aspect resolves the acting `User`, the parent entity ID, and the child entity ID from the method arguments and return value.
4. `ActivityDetailsBuilder` extracts action-specific context (titles, grades, file names, etc.) into a `details` map.
5. `ActivityLogService.log(...)` persists the `ActivityLog` record in a **separate transaction** (`REQUIRES_NEW`), so the log is written even if the main transaction rolls back.
6. If the intercepted method threw an exception, the log is saved with `status = FAILED` and the `details` map includes `"failed": true` and `"errorType"`.

---

## The `@LogActivity` Annotation

```java
@LogActivity(
    action          = ActivityAction.CREATED,   // required
    entityType      = EntityType.ASSIGNMENT,    // required
    parentIdParamIndex = 0,                     // index of the parent-entity argument (default 0)
    orphan          = false,                    // true → use the new entity itself as parentId
    actorParamIndex = -1                        // index of the User argument; -1 = auto-detect first User
)
```

| Attribute | Type | Default | Description |
|---|---|---|---|
| `action` | `ActivityAction` | — | The action being performed |
| `entityType` | `EntityType` | — | The type of entity being acted on |
| `parentIdParamIndex` | `int` | `0` | Index of the method parameter that holds the parent entity (or its UUID) |
| `orphan` | `boolean` | `false` | When `true`, the returned entity's own ID is used as `parentId` (for top-level entities with no natural parent) |
| `actorParamIndex` | `int` | `-1` | Index of the `User` parameter; `-1` means the first `User` found in the argument list |

### Annotation target

`@LogActivity` is a **method-level** annotation and must be placed on a Spring-managed bean method so the AOP proxy can intercept it.

---

## Enums

### `ActivityAction`
`CREATED` · `ASSIGNED` · `COMMENTED` · `UPDATED` · `CLOSED` · `ADDED` · `REMOVED` · `EVALUATED`

### `EntityType`
`ASSIGNMENT` · `CLASS_ENROLLMENT` · `COMMENT` · `COMMENT_FILE` · `COURSE` · `FILE` · `ROLE` · `SCHOOL_CLASS` · `SUBMISSION` · `USER` · `USER_ASSIGNMENT`

### `ActivityStatus`
`SUCCESS` · `FAILED`

---

## `ActivityLog` Entity

| Column | Type | Description |
|---|---|---|
| `id` | `UUID` | Primary key |
| `user` | `User` (FK) | The actor who performed the action |
| `parentId` | `UUID` | ID of the owning/parent entity |
| `action` | `ActivityAction` | The action performed |
| `entityType` | `EntityType` | Type of the child entity |
| `childId` | `UUID` | ID of the entity that was created/modified |
| `details` | `JSONB` | Action-specific context (see below) |
| `timestamp` | `LocalDateTime` | When the action occurred |
| `status` | `ActivityStatus` | `SUCCESS` or `FAILED` |

---

## Details Map by Action

`ActivityDetailsBuilder` populates the `details` JSONB column with fields that depend on the `action` + `entityType` combination.

| Action | EntityType | Fields |
|---|---|---|
| `CREATED` | `ASSIGNMENT` | `title` |
| `CREATED` | `COURSE` | `name`, `class` |
| `ASSIGNED` | `USER_ASSIGNMENT` | `assignmentTitle`, `student` (username) |
| `UPDATED` | `COURSE` | `newLeadTeacher` (username) |
| `EVALUATED` | `USER_ASSIGNMENT` | `grade`, `feedback` (first 80 chars) |
| `ADDED` | `COMMENT` | `assignmentTitle`, `preview` (first 80 chars) |
| `ADDED` | `FILE` | `fileName`, `contentType`, `fileSize` |
| `ADDED` | `COMMENT_FILE` | `fileName`, `commentId`, `assignmentTitle` |
| any | any (on failure) | `failed: true`, `errorType` |

---

## Adding Logging to a New Method

### 1. Normal case — method receives the parent entity as an argument

```java
// parentIdParamIndex=0 → assignment is argument[0]; first User found is the actor
@LogActivity(action = ActivityAction.ADDED, entityType = EntityType.COMMENT, parentIdParamIndex = 0)
public Comment addComment(Assignment assignment, User author, String text) { ... }
```

### 2. Top-level entity with no parent (orphan)

```java
// orphan=true → the returned Assignment's own ID becomes parentId
@LogActivity(action = ActivityAction.CREATED, entityType = EntityType.ASSIGNMENT, orphan = true)
public Assignment createCase(String title, String description, User creator, Course course) { ... }
```

### 3. Explicit actor index

```java
// actorParamIndex=4 → argument[4] is the acting User, not leadTeacher
@LogActivity(action = ActivityAction.CREATED, entityType = EntityType.COURSE, orphan = true, actorParamIndex = 4)
public Course createCourse(String name, String description, SchoolClass schoolClass, User leadTeacher, User creator) { ... }
```

### 4. Extend `ActivityDetailsBuilder` if needed

If the new action/entityType combination requires specific context fields, add a branch to `ActivityDetailsBuilder.build()`. Keep the return type `Map<String, Object>`.

---

## REST API

Base path: `/api/activity-logs`

### `GET /api/activity-logs/user/{userId}`

Returns paginated logs for a specific user, ordered by newest first.

- **Path param:** `userId` (UUID)
- **Query params:** standard `Pageable` (`page`, `size`, `sort`)
- **Authorization:** the requesting user must be the same user **or** an admin
- **Response:** `Page<ActivityLogResponse>`

### `GET /api/activity-logs/entity/{entityType}/{entityId}`

Returns paginated logs for a specific entity, ordered by newest first.

- **Path params:** `entityType` (enum name, e.g. `ASSIGNMENT`), `entityId` (UUID)
- **Query params:** standard `Pageable`
- **Authorization:** admin only
- **Response:** `Page<ActivityLogResponse>`

### `ActivityLogResponse` fields

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Log entry ID |
| `action` | ActivityAction | The action performed |
| `entityType` | EntityType | Type of entity |
| `entityId` | UUID | ID of the affected entity (`childId`) |
| `details` | `Map<String, Object>` | Action-specific context |
| `timestamp` | LocalDateTime | When it happened |

---

## Key Design Decisions

- **`REQUIRES_NEW` transaction** — the log write always commits independently, so a rollback in the calling service does not swallow the failure log.
- **`orphan = true`** — used for top-level entities (assignments, courses, classes) that have no natural parent; their own ID is stored as `parentId` so all related child logs can be queried together later.
- **Auto actor detection** — if `actorParamIndex` is `-1` the aspect scans the argument list for the first `User` instance, which works for the majority of service methods without extra configuration.
