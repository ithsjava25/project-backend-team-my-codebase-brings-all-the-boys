package org.example.projectbackendteammycodebasebringsalltheboys.logging;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Identifiable;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ActivityLogService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogService activityLogService;
    private final ActivityDetailsBuilder detailsBuilder;

    @Pointcut("@annotation(org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity)")
    public void logActivityPointcut() {}


    @AfterReturning(pointcut = "logActivityPointcut() && @annotation(logActivity)", returning = "result")
    public void logActivity(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            User user = resolveUser(args);

            if (user == null) {
                log.warn("Activity log skipped — no User found for action {}", logActivity.action());
                return;
            }

            Long caseId = logActivity.noCase() ? resolveResultId(result)
                    : resolveCaseId(args, logActivity);

            Long entityId = result instanceof Identifiable i ? i.getId()
                    : null;

            Map<String, Object> details = detailsBuilder.build(logActivity, args);

            activityLogService.log(user, caseId, logActivity.action(), logActivity.entity(),
                    entityId, details, ActivityStatus.SUCCESS);

        } catch (Exception e) {
            log.error("Failed to write activity log", e);
        }
    }

    @AfterThrowing(pointcut = "logActivityPointcut() && @annotation(logActivity)", throwing = "ex")
    public void logFailedActivity(JoinPoint joinPoint, LogActivity logActivity, Exception ex) {
        try {
            Object[] args = joinPoint.getArgs();
            User user = resolveUser(args);

            if (user == null) {
                log.warn("Failed activity log skipped — no User found for action {}", logActivity.action());
                return;
            }
            Long caseId = resolveCaseId(args, logActivity);

            Map<String, Object> details = detailsBuilder.build(logActivity, args);
            details.put("failed", true);
            details.put("error", ex.getMessage());

            activityLogService.log(user, caseId, logActivity.action(), logActivity.entity(),
                    null, details, ActivityStatus.FAILED);

        } catch (Exception e) {
            log.error("Failed to write failure activity log", e);
        }
    }

    private User resolveUser(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof User user) return user;
        }
        return null;
    }

    private Long resolveCaseId(Object[] args, LogActivity logActivity) {
        Object param = args[logActivity.caseIdParamIndex()];
        if (param instanceof Long id) return id;
        if (param instanceof Comment comment) return comment.getAssignment().getId();
        if (param instanceof Assignment assignment) return assignment.getId();
        return null;
    }

    private Long resolveResultId(Object result) {
        if (result instanceof Long id) return id;
        if (result instanceof Identifiable i) return i.getId();
        return null;
    }
}