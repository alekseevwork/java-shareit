package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Collection<User> findAll();

    Optional<User> findUserById(Long userId);

    User update(User user, Long userId);

    void delete(Long userId);

    void isEmailExist(String email);
}
