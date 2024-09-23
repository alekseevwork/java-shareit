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
    public User create(UserDto userDto) {
        return repository.save(UserMapper.toUser(userDto));
    }

    @Override
    public Collection<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
    }

    @Override
    @Transactional
    public User update(UserDto userDto, Long userId) {
        User oldUser = findUserById(userId);
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }
        return repository.save(oldUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        repository.deleteById(userId);
    }
}
