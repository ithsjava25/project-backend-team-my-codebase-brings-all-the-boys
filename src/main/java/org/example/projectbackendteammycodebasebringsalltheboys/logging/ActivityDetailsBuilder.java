package org.example.projectbackendteammycodebasebringsalltheboys.logging;

import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ActivityDetailsBuilder {

    public Map<String, Object> build(LogActivity logActivity, Object[] args) {
        Map<String, Object> details = new LinkedHashMap<>();

        switch (logActivity.action()) {
            case CREATED -> handleCreated(logActivity.entity(), args, details);
            case ADDED   -> handleAdded(logActivity.entity(), args, details);
            default      -> details.put("action", logActivity.action().name());
        }

        return details;
    }

    private void handleCreated(EntityType entityType, Object[] args, Map<String, Object> details) {
        switch (entityType) {
            case ASSIGNMENT -> {
                // createCase(String title, String description, User creator)
                findFirst(args, String.class).ifPresent(title -> details.put("title", title));
            }
            default -> {}
        }
    }

    private void handleAdded(EntityType entityType, Object[] args, Map<String, Object> details) {
        switch (entityType) {
            case COMMENT -> {
                // addComment(Assignment assignment, User author, String text)
                findFirst(args, Assignment.class)
                        .ifPresent(a -> details.put("assignmentTitle", a.getTitle()));
                findFirst(args, String.class)
                        .ifPresent(text -> details.put("preview", preview(text)));
            }
            case FILE -> {
                // uploadAssignmentFile(Assignment, User, String fileName, InputStream, long, String contentType)
                List<String> strings = findAll(args, String.class);
                if (!strings.isEmpty()) details.put("fileName", strings.get(0));
                if (strings.size() > 1)  details.put("contentType", strings.get(1));
                findFirst(args, Long.class).ifPresent(size -> details.put("fileSize", size));
            }
            case COMMENT_FILE -> {
                // uploadCommentFile(Comment, User, String fileName, InputStream, long, String contentType)
                findFirst(args, String.class).ifPresent(name -> details.put("fileName", name));
                findFirst(args, Comment.class).ifPresent(c -> {
                    details.put("commentId", c.getId());
                    details.put("assignmentTitle", c.getAssignment().getTitle());
                });
            }
            default -> {}
        }
    }

    // --- helpers ---

    private <T> Optional<T> findFirst(Object[] args, Class<T> type) {
        return Arrays.stream(args)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    private <T> List<T> findAll(Object[] args, Class<T> type) {
        return Arrays.stream(args)
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    private String preview(String text) {
        return text.length() > 80 ? text.substring(0, 80) + "…" : text;
    }
}