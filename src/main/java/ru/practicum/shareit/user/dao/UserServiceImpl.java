package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicatedMailException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    HashMap<Long, User> userMap = new HashMap<>();
    Long id = 1L;

    @Override
    public User create(User user) {
        user.setId(id++);
        isEmailExist(user.getEmail());
        if (user.getName().isBlank() || user.getEmail().isBlank()) {
            throw new ValidationException("Name is null!");
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userMap.values();
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.of(userMap.get(userId));
    }

    @Override
    public User update(User userUpd, Long userId) {
        userUpd.setId(userId);
        if (userMap.values().stream().anyMatch(user -> user.getEmail().equals(userUpd.getEmail()))) {
            throw new DuplicatedMailException("");
        }
        userMap.put(userId, userUpd);
        return userUpd;
    }

    @Override
    public void delete(Long userId) {
        userMap.remove(userId);
    }

    @Override
    public void isEmailExist(String email) {
        if (userMap.values().stream().filter(user -> user.getEmail().equals(email)).count() == 1)
            throw new DuplicatedMailException("Email " + email + " is already exist");
    }
}
