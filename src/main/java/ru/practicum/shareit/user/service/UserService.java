package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User create(UserDto user);

    Collection<User> findAll();

    User findUserById(Long userId);

    User update(UserDto user, Long userId);

    void delete(Long userId);
}
