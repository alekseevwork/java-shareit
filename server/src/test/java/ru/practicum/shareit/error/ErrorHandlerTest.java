package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.error.exeption.DuplicatedMailException;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleValidation() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorResponse errorResponse = errorHandler.handleValidation(exception);

        assertEquals("Validation error", errorResponse.error());
    }

    @Test
    void handleNotFound() {
        NotFoundException exception = new NotFoundException("Not found");

        ErrorResponse errorResponse = errorHandler.handleNotFound(exception);

        assertEquals("Not found", errorResponse.error());
    }

    @Test
    void handleDuplicatedMail() {
        DuplicatedMailException exception = new DuplicatedMailException("Duplicated mail");

        ErrorResponse errorResponse = errorHandler.handleDuplicatedMail(exception);

        assertEquals("Duplicated mail", errorResponse.error());
    }

    @Test
    void handleThrowable() {
        Throwable exception = new Throwable("Internal server error");

        ErrorResponse errorResponse = errorHandler.handleThrowable(exception);

        assertEquals("Internal server error", errorResponse.error());
    }
}
