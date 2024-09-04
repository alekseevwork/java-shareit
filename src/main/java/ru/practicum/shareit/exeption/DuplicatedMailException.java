package ru.practicum.shareit.exeption;

public class DuplicatedMailException extends RuntimeException {
    public DuplicatedMailException(String message) {
        super(message);
    }
}