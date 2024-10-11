package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@Positive @PathVariable Long userId) {
        log.info("GET /users/userId: getById - {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET /users: getAll");
        return userClient.getAllUser();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user) {
        log.info("POST /users: create: {}", user);
        return userClient.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto newUser) {
        log.info("PATCH /users: update: {}, by id - {}", newUser, userId);
        return userClient.update(userId, newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@Positive @PathVariable Long userId) {
        log.info("DELETE /users/userId: deleteById - {}", userId);
        userClient.delete(userId);
    }
}
