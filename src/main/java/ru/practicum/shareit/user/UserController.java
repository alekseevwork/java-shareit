package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserRepositoryInMemory userRepository;

    @GetMapping("/{userId}")
    public UserDto findAById(@PathVariable Long userId) {
        log.info("GET /users: findAById by id - {}", userId);
        return userRepository.findUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("GET /users: findAll");
        return userRepository.findAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody User user) {
        log.info("POST /users: create: {}", user);
        return userRepository.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody User newUser, @PathVariable Long userId) {
        log.info("PUT /users: update: {}", newUser);
        return userRepository.update(newUser, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("GET /users: findAll");
        userRepository.delete(userId);
    }

}
