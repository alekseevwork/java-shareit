package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exeption.DuplicatedMailException;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @SneakyThrows
    @Test
    public void create_whenInvoked_thenResponseStatusOkWithUserInBody() {
        UserDto userDto = new UserDto(1L, "Name", "email@email.com");
        User userSaved = new User(1L, "Name", "email@email.com");
        when(userService.create(userDto))
                .thenReturn(userSaved);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userSaved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userSaved), result);

        verify(userService).create(userDto);
    }

    @SneakyThrows
    @Test
    public void create_whenInvokedAndEmailIsDuplicate_thenResponseStatusConflictAndThrownDuplicatedMailException() {
        UserDto userDto = new UserDto(1L, "Name", "email@email.com");
        User userSaved = new User(1L, "Name", "email@email.com");
        when(userService.create(userDto))
                .thenThrow(new DuplicatedMailException("Email - " + userDto.getEmail() + " already exist."));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userSaved)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Email - " + userDto.getEmail() + " already exist."));

        verify(userService).create(userDto);
    }


    @SneakyThrows
    @Test
    public void findAll_whenInvoked_thenStatusOkWithListUserInBody() {
        List<User> users = List.of(new User());
        when(userService.findAll()).thenReturn(users);

        String response = mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(users), response);

        verify(userService).findAll();
    }

    @SneakyThrows
    @Test
    public void findUserById_whenInvoked_thenResponseStatusOk() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findUserById(userId);

    }

    @SneakyThrows
    @Test
    public void update_whenInvoked_thenReturnStatusOkAndUser() {
        long userId = 1L;
        User user = new User(1L, "Name", "email@email.com");
        UserDto userDto = new UserDto(1L, "Name", "email@email.com");
        when(userService.update(userDto, userId)).thenReturn(user);

        String response = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), response);

        verify(userService).update(userDto, userId);
    }

    @SneakyThrows
    @Test
    public void update_whenInvokedAndUserNotFound_thenReturnStatusNotFoundAndNotFoundException() {
        long userId = 1L;
        User user = new User(1L, "Name", "email@email.com");
        UserDto userDto = new UserDto(1L, "Name", "email@email.com");
        when(userService.update(userDto, userId))
                .thenThrow(new NotFoundException("User by id -" + userId + " not found."));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User by id -" + userId + " not found."));

        verify(userService).update(userDto, userId);
    }

    @SneakyThrows
    @Test
    public void deleteById_whenInvoked_thenStatusOk() {
        long userId = 1L;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}