package ru.practicum.shareit.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String s) {
        super(s);
    }
}
