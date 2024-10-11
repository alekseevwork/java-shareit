package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
        // Given
        ValidationException exception = new ValidationException("Validation error");

        // When
        ErrorResponse errorResponse = errorHandler.handleValidation(exception);

        // Then
//        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Validation error", errorResponse.error());
    }

    @Test
    void handleNotFound() {
        // Given
        NotFoundException exception = new NotFoundException("Not found");

        // When
        ErrorResponse errorResponse = errorHandler.handleNotFound(exception);

        // Then
//        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not found", errorResponse.error());
    }

    @Test
    void handleDuplicatedMail() {
        // Given
        DuplicatedMailException exception = new DuplicatedMailException("Duplicated mail");

        // When
        ErrorResponse errorResponse = errorHandler.handleDuplicatedMail(exception);

        // Then
//        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Duplicated mail", errorResponse.error());
    }

    @Test
    void handleThrowable() {
        // Given
        Throwable exception = new Throwable("Internal server error");

        // When
        ErrorResponse errorResponse = errorHandler.handleThrowable(exception);

        // Then
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.);
        assertEquals("Internal server error", errorResponse.error());
    }
}
