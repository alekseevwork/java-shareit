package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    User create(UserDto user);

    List<User> findAll();

    User findUserById(Long userId);

    User update(UserDto user, Long userId);

    void delete(Long userId);
}
