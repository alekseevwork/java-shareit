package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dao.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;

@Repository
public class UserRepositoryInMemory {
    @Autowired
    UserService userService;

    public UserDto create(User user) {
        userService.create(user);
        return UserMapper.toUserDto(user);
    }

    public Collection<UserDto> findAll() {
        return userService.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public UserDto findUserById(Long userId) {
        return userService.findUserById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
    }

    public UserDto update(User user, Long userId) {
        userService.update(user, userId);
        return UserMapper.toUserDto(user);
    }

    public void delete(Long userId) {
        userService.delete(userId);
    }

    public void isEmailExist(String email) {

    }
}
