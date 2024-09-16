package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceDb implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toDto(user);
    }

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.toDto(repository.findAll());
    }

    @Override
    public UserDto findUserById(Long userId) {
        return repository.findById(userId)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        UserDto oldUser = findUserById(userId);
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        User user = repository.save(UserMapper.toUser(oldUser));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        repository.deleteById(userId);
    }
}
