package ru.practicum.shareit.error.exeption;

public class DuplicatedMailException extends RuntimeException {
    public DuplicatedMailException(String message) {
        super(message);
    }
}