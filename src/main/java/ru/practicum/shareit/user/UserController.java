package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("GET /users/userId: getById - {}", userId);
        return UserMapper.toDto(userService.findUserById(userId));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("GET /users: getAll");
        return UserMapper.toDto(userService.findAll());
    }

    @PostMapping
    public UserDto create(@Validated @RequestBody UserDto user) {
        log.info("POST /users: create: {}", user);
        return UserMapper.toDto(userService.create(user));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto newUser) {
        log.info("PATCH /users: update: {}, by id - {}", newUser, userId);
        return UserMapper.toDto(userService.update(newUser, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE /users/userId: deleteById - {}", userId);
        userService.delete(userId);
    }
}
