package com.javarush.jira.bugtracking.task;

import com.javarush.jira.bugtracking.Handlers;
import com.javarush.jira.bugtracking.task.to.ActivityTo;
import com.javarush.jira.common.error.DataConflictException;
import com.javarush.jira.login.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.javarush.jira.bugtracking.task.TaskUtil.getLatestValue;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final static String READY_FOR_REVIEW = "ready_for_review";
    private final static String IN_PROGRESS = "in_progress";
    private final static String DONE = "done";
    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;
    private final Handlers.ActivityHandler handler;

    private static void checkBelong(HasAuthorId activity) {
        if (activity.getAuthorId() != AuthUser.authId()) {
            throw new DataConflictException("Activity " + activity.getId() + " doesn't belong to " + AuthUser.get());
        }
    }

    @Transactional
    public Activity create(ActivityTo activityTo) {
        checkBelong(activityTo);
        Task task = taskRepository.getExisted(activityTo.getTaskId());
        if (activityTo.getStatusCode() != null) {
            task.checkAndSetStatusCode(activityTo.getStatusCode());
        }
        if (activityTo.getTypeCode() != null) {
            task.setTypeCode(activityTo.getTypeCode());
        }
        return handler.createFromTo(activityTo);
    }

    @Transactional
    public void update(ActivityTo activityTo, long id) {
        checkBelong(handler.getRepository().getExisted(activityTo.getId()));
        handler.updateFromTo(activityTo, id);
        updateTaskIfRequired(activityTo.getTaskId(), activityTo.getStatusCode(), activityTo.getTypeCode());
    }

    @Transactional
    public void delete(long id) {
        Activity activity = handler.getRepository().getExisted(id);
        checkBelong(activity);
        handler.delete(activity.id());
        updateTaskIfRequired(activity.getTaskId(), activity.getStatusCode(), activity.getTypeCode());
    }

    private void updateTaskIfRequired(long taskId, String activityStatus, String activityType) {
        if (activityStatus != null || activityType != null) {
            Task task = taskRepository.getExisted(taskId);
            List<Activity> activities = handler.getRepository().findAllByTaskIdOrderByUpdatedDesc(task.id());
            if (activityStatus != null) {
                String latestStatus = getLatestValue(activities, Activity::getStatusCode);
                if (latestStatus == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setStatusCode(latestStatus);
            }
            if (activityType != null) {
                String latestType = getLatestValue(activities, Activity::getTypeCode);
                if (latestType == null) {
                    throw new DataConflictException("Primary activity cannot be delete or update with null values");
                }
                task.setTypeCode(latestType);
            }
        }
    }

    public long taskSummaryDurationBeforeReview(long id) {
        return getTaskStatusDuration(id, IN_PROGRESS, READY_FOR_REVIEW);
    }

    public long taskSummaryDurationBeforeDone(long id) {
        return getTaskStatusDuration(id, READY_FOR_REVIEW, DONE);
    }

    private long getTaskStatusDuration(long id, String statusCode1, String statusCode2) {
        List<Activity> allById = activityRepository.findAllByTaskId(id);
        Optional<LocalDateTime> startTask = allById
                .stream()
                .filter(a -> Objects.nonNull(a.getStatusCode()))
                .filter(a -> a.getStatusCode().equals(statusCode1))
                .map(Activity::getUpdated)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> restartTask = allById
                .stream()
                .filter(a -> Objects.nonNull(a.getStatusCode()))
                .filter(a -> a.getStatusCode().equals(statusCode1))
                .map(Activity::getUpdated)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        Optional<LocalDateTime> stopTask = allById
                .stream()
                .filter(a -> Objects.nonNull(a.getStatusCode()))
                .filter(a -> a.getStatusCode().equals(statusCode2))
                .map(Activity::getUpdated)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (startTask.isPresent()
                && stopTask.isPresent()
                && restartTask.isPresent()) {
            if (restartTask.get().isBefore(stopTask.get())) {
                return Duration.between(startTask.get(), stopTask.get()).toMillis();
            }
        }
        return 0;
    }
}
