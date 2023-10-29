insert into USERS (ID, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, DISPLAY_NAME)
values (999, 'activity_test@gmail.com', '{noop}test', 'test', 'test', 'test');

insert into PROJECT (ID, code, title, description, type_code, parent_id)
values (999, 'TEST_PR', 'PROJECT-1', 'test project 1', 'task_tracker', null);

insert into SPRINT (ID, status_code, startpoint, endpoint, code, project_id)
values (999, 'finished', '2023-05-01 08:05:10', '2023-05-07 17:10:01', 'SP-1.001', 999);

insert into TASK (ID, TITLE, TYPE_CODE, STATUS_CODE, PROJECT_ID, SPRINT_ID, STARTPOINT)
values (999, 'Test', 'epic', 'in_progress', 999, 999, '2023-05-15 09:05:10');

insert into ACTIVITY(AUTHOR_ID, TASK_ID, UPDATED, COMMENT, TITLE, DESCRIPTION, ESTIMATE, TYPE_CODE, STATUS_CODE,
                     PRIORITY_CODE)
values (999, 999, '2023-05-15 09:05:10', null, 'Test', null, 3, 'epic', 'in_progress', 'low'),
       (999, 999, '2023-05-17 09:05:10', null, 'Test', null, 3, 'epic', 'in_progress', 'low'),
       (999, 999, '2023-05-18 09:05:10', null, 'Test', null, 3, 'epic', 'ready_for_review', 'low'),
       (999, 999, '2023-05-19 09:05:10', null, 'Test', null, 3, 'epic', 'done', 'low');

