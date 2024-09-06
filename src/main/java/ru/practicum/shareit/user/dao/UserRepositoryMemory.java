package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeption.DuplicatedMailException;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service("userRepositoryMemory")
@RequiredArgsConstructor
public class UserRepositoryMemory implements UserRepository {

    HashMap<Long, User> userMap = new HashMap<>();
    Long id = 1L;

    @Override
    public User create(User user) {
        log.info("created user - {}", user);
        user.setId(id++);
        isEmailExist(user.getEmail());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userMap.values();
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public User update(User userUpd, Long userId) {
        if (!userMap.containsKey(userId) || userId == null) {
            log.debug("Not user by id - {}", userId);
            throw new NotFoundException("User by id - " + userId + " not found");
        }
        User user = findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        if (userUpd.getName() != null) {
            user.setName(userUpd.getName());
        }
        if (userUpd.getEmail() != null) {
            isEmailExist(userUpd.getEmail());
            user.setEmail(userUpd.getEmail());
        }
        userMap.put(userId, user);
        log.info("update user - {}", user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.info("Delete user by id {}", userId);
        userMap.remove(userId);
    }

    @Override
    public void isEmailExist(String email) {
        if (userMap.values().stream().filter(user -> user.getEmail().equals(email)).count() == 1) {
            log.debug("Email {} is already exist", email);
            throw new DuplicatedMailException("Email " + email + " is already exist");
        }
    }
}
