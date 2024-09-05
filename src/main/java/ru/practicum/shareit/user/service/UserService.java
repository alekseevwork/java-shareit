package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto create(User user);

    Collection<UserDto> findAll();

    UserDto findUserById(Long userId);

    UserDto update(User user, Long userId);

    void delete(Long userId);
}
