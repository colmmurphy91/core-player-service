package com.cluborg.coreplayerservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerValidationException extends ResponseStatusException {

    public PlayerValidationException(HttpStatus status) {
        super(status);
    }

    public PlayerValidationException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public PlayerValidationException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
