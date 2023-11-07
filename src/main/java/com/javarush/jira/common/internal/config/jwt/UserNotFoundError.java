package com.javarush.jira.common.internal.config.jwt;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserNotFoundError {
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public UserNotFoundError(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
