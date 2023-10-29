package com.javarush.jira.bugtracking.task;

import com.javarush.jira.AbstractControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;


public class ActivityServiceTest extends AbstractControllerTest {
    @Autowired
    ActivityService activityService;

    @Test
    @Sql(value = "/test_db_data/activity-duration-false.sql")
    @Transactional
    public void shouldReturn0IfReviewBeforeInProgress(){
        long expected = 0;
        long actual = activityService.taskSummaryDurationBeforeReview(999);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(value = "/test_db_data/activity-duration-true.sql")
    @Transactional
    public void shouldCountTaskSummaryDurationIfReviewAfterInProgress(){
        long expected = 259200000;
        long actual = activityService.taskSummaryDurationBeforeReview(999);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(value = "/test_db_data/activity-duration-false.sql")
    @Transactional
    public void shouldReturn0IfDoneBeforeReadyForReview(){
        long expected = 0;
        long actual = activityService.taskSummaryDurationBeforeDone(999);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(value = "/test_db_data/activity-duration-true.sql")
    @Transactional
    public void shouldCountTaskSummaryDurationIfDoneAfterReadyForReview(){
        long expected = 86400000;
        long actual = activityService.taskSummaryDurationBeforeDone(999);
        Assertions.assertEquals(expected, actual);
    }

}
