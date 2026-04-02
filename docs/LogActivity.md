# @LogActivity Annotation

Annotate any service method to automatically log who performed an action, which assignment it belongs to, and what entity was affected. Failed executions are also logged automatically with the error message included.

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `action` | `ActivityAction` | required | The type of action performed |
| `entity` | `EntityType` | required | The type of entity affected |
| `caseIdParamIndex` | `int` | `0` | Index of the parameter used to resolve the assignment ID |
| `noCase` | `boolean` | `false` | Set to `true` when the method creates the assignment itself |

## Rules

- The method must have a `User` parameter — the aspect finds it automatically regardless of position
- The return value is automatically used as the `entityId`, so the method must return the created or modified entity
- `caseIdParamIndex` points to the parameter that owns the assignment — either an `Assignment` or a `Comment`. Defaults to `0`
- Use `noCase = true` when the entity being created **is** the assignment itself, since the ID can only be known after the method returns

## Usage

### Creating an assignment
The returned `Assignment` is used for both `assignmentId` and `entityId` since no assignment exists yet at the time of the call.
```java
@LogActivity(action = ActivityAction.CREATED, entity = EntityType.ASSIGNMENT, noCase = true)
public Assignment createCase(String title, String description, User creator) { ... }
```

### Adding a comment to an assignment
`args[0]` is an `Assignment`, so `assignmentId` resolves to `assignment.getId()`.
```java
@LogActivity(action = ActivityAction.ADDED, entity = EntityType.COMMENT)
public Comment addComment(Assignment assignment, User author, String text) { ... }
```

### Uploading a file to an assignment
`args[0]` is an `Assignment`, so `assignmentId` resolves to `assignment.getId()`.
```java
@LogActivity(action = ActivityAction.ADDED, entity = EntityType.FILE)
public FileMetadata uploadAssignmentFile(Assignment assignment, User uploader, ...) { ... }
```

### Uploading a file to a comment
`args[0]` is a `Comment`, so `assignmentId` resolves to `comment.getAssignment().getId()`.
```java
@LogActivity(action = ActivityAction.ADDED, entity = EntityType.COMMENT_FILE)
public FileMetadata uploadCommentFile(Comment comment, User uploader, ...) { ... }
```

### Assignment-owning parameter is not at index 0
Use `caseIdParamIndex` to point to the correct parameter.
```java
@LogActivity(action = ActivityAction.ADDED, entity = EntityType.FILE, caseIdParamIndex = 1)
public FileMetadata someMethod(User uploader, Assignment assignment, ...) { ... }
```

## What gets logged

Each log entry contains:

| Field | Description |
|---|---|
| `user` | The user who performed the action |
| `caseId` | The assignment the action belongs to |
| `entityId` | The ID of the created or modified entity |
| `entityType` | The type of entity affected |
| `action` | The action performed |
| `status` | `SUCCESS` or `FAILED` |
| `details` | Structured JSON with action-specific context |
| `timestamp` | When the action occurred |

### Example details per action

```json
// CREATED ASSIGNMENT
{ "title": "Inlämningsuppgift 3 - Datastrukturer" }

// ADDED COMMENT
{ "assignmentTitle": "Inlämningsuppgift 3 - Datastrukturer", "preview": "Jag har laddat upp min lösning..." }

// ADDED FILE
{ "fileName": "uppgift3.pdf", "contentType": "application/pdf", "fileSize": 204800 }

// ADDED COMMENT_FILE
{ "fileName": "bilaga.png", "commentId": 7, "assignmentTitle": "Inlämningsuppgift 3 - Datastrukturer" }

// FAILED (any action)
{ "...", "failed": true, "error": "Assignment not found" }
```

## Adding a new loggable action

1. Add the action to `ActivityAction` if it does not already exist
2. Add the entity type to `EntityType` if it does not already exist
3. Add a `case` block in `ActivityDetailsBuilder` for the new combination
4. Annotate the method