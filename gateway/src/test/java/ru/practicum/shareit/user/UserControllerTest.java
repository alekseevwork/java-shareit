package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void before() {
        userDto = new UserDto(1L, "User ", "user@example.com");
    }

    @Test
    void getById_whenUserFound_thenStatusOkAndReturnUser() throws Exception {
        when(userClient.getUserById(1L)).thenReturn(ResponseEntity.ok(userDto));

        MvcResult result = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"name\":\"User \",\"email\":\"user@example.com\"}", responseBody);
    }

    @Test
    void getById_InvalidId_thenStatusNotFound() throws Exception {
        when(userClient.getUserById(2L)).thenReturn(ResponseEntity.notFound().build());

        MvcResult result = mockMvc.perform(get("/users/2"))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("", responseBody);
    }

    @Test
    void getAll_whenInvoked_thenStatusOkAndReturnListUserDto() throws Exception {
        when(userClient.getAllUser()).thenReturn(ResponseEntity.ok(List.of(userDto)));

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"name\":\"User \",\"email\":\"user@example.com\"}]", responseBody);
    }

    @Test
    void create_whenUserValid_thenStatusOkAndReturnUserDto() throws Exception {
        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"name\":\"User \",\"email\":\"user@example.com\"}", responseBody);
    }

    @Test
    void create_whenInvalidUser_thenStatusBadRequest() throws Exception {
        userDto = new UserDto(1L, "", "user@example.com");
        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(userClient, never()).create(userDto);
    }

    @Test
    void update_whenValidRequest_thenStatusOkAndReturnUser() throws Exception {
        when(userClient.update(anyLong(), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        MvcResult result = mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"name\":\"User \",\"email\":\"user@example.com\"}", responseBody);
    }

    @Test
    void update_whenInvalidId_thenStatusNotFound() throws Exception {
        when(userClient.update(anyLong(), any(UserDto.class))).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void deleteById_whenInvoked_thenStatusOk() throws Exception {
        doNothing().when(userClient).delete(anyLong());

        MvcResult result = mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("", responseBody);
    }
}
