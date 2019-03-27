package com.cluborg.coreplayerservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerAlreadyExistsException extends ResponseStatusException {

    public PlayerAlreadyExistsException(HttpStatus status) {
        super(status);
    }

    public PlayerAlreadyExistsException(HttpStatus status, String reason) {
        super(status, reason);
    }


    public PlayerAlreadyExistsException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
