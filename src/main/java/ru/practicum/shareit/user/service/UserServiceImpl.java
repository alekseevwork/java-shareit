package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    UserRepository repository;

    public UserServiceImpl(@Qualifier("userRepositoryMemory") UserRepository repository) {
        this.repository = repository;
    }

    public UserDto create(UserDto user) {
        User newUser = UserMapper.toUser(user);
        return UserMapper.toDto(repository.create(newUser));
    }

    public Collection<UserDto> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto findUserById(Long userId) {
        return repository.findUserById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
    }

    public UserDto update(UserDto user, Long userId) {
        User updUser = UserMapper.toUser(user);
        return UserMapper.toDto(repository.update(updUser, userId));
    }

    public void delete(Long userId) {
        repository.delete(userId);
    }
}
