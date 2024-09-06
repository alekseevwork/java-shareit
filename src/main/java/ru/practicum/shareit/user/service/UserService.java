package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto user);

    Collection<UserDto> findAll();

    UserDto findUserById(Long userId);

    UserDto update(UserDto user, Long userId);

    void delete(Long userId);
}
