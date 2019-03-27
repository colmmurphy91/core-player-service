package com.cluborg.coreplayerservice.exceptions;

public class PlayerDoesNotExistException extends Exception {
    public PlayerDoesNotExistException(String message) {
        super(message);
    }
}
