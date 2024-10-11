package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceImplTest {

    @MockBean
    private UserRepository repository;

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder()
                .name("Name")
                .email("email@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name("Name")
                .email("email@email.com")
                .build();

        when(repository.save(any(User.class))).thenReturn(user);

        User createdUser  = userService.create(userDto);

        assertEquals(user, createdUser );
    }

    @Test
    void findAllUsers() {
        List<User> users = List.of(
                User.builder().id(1L).name("Name1").email("email1@email.com").build(),
                User.builder().id(2L).name("Name2").email("email2@email.com").build()
        );

        when(repository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertEquals(users, foundUsers);
    }

    @Test
    void findUserById() {
        User user = User.builder()
                .id(1L)
                .name("Name")
                .email("email@email.com")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser  = userService.findUserById(1L);

        assertEquals(user, foundUser );
    }

    @Test
    void findUserByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void updateUser() {
        UserDto userDto = UserDto.builder()
                .name("New Name")
                .email("newemail@email.com")
                .build();

        User oldUser  = User.builder()
                .id(1L)
                .name("Old Name")
                .email("oldemail@email.com")
                .build();

        User updatedUser  = User.builder()
                .id(1L)
                .name("New Name")
                .email("newemail@email.com")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(oldUser ));
        when(repository.save(any(User.class))).thenReturn(updatedUser );

        User user = userService.update(userDto, 1L);

        assertEquals(updatedUser , user);
    }

    @Test
    void deleteUser() {
        Long userId = 1L;

        userService.delete(userId);

        verify(repository, times(1)).deleteById(userId);
    }
}