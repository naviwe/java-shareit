package ru.practicum.shareit.error;

public class IncorrectStateException extends RuntimeException {
    public IncorrectStateException(String message) {
        super(message);
    }
}
