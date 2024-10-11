package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenResponseStatusOkAndReturnItemRequestDto() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.create(1L, dto)).thenReturn(expectedDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(expectedDto));
    }

    @SneakyThrows
    @Test
    void getAllItemRequestByUserId_whenInvoked_thenResponseOkAndReturnListRequests() {
        List<ItemRequestAnswerDto> expectedDtos = List.of(
                ItemRequestAnswerDto.builder()
                        .id(1L)
                        .description("description")
                        .build(),
                ItemRequestAnswerDto.builder()
                        .id(2L)
                        .description("description")
                        .build()
        );

        when(itemRequestService.getAllItemRequestByUserId(1L)).thenReturn(expectedDtos);

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(expectedDtos));
    }

    @SneakyThrows
    @Test
    void getAllItemRequestExceptUserId_whenInvoked_thenResponseStatusOkAndListItemRequestAnswerDto() {
        List<ItemRequestAnswerDto> expectedDtos = List.of(
                ItemRequestAnswerDto.builder()
                        .id(1L)
                        .description("description")
                        .build(),
                ItemRequestAnswerDto.builder()
                        .id(2L)
                        .description("description")
                        .build()
        );

        when(itemRequestService.getAllItemRequestExceptUserId(1L)).thenReturn(expectedDtos);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(expectedDtos));
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenResponseStatusOkAndReturnItemRequestAnswerDto() {
        ItemRequestAnswerDto expectedDto = ItemRequestAnswerDto.builder()
                .id(1L)
                .description("description")
                .build();

        when(itemRequestService.getById(1L)).thenReturn(expectedDto);

        String result = mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(expectedDto));
    }
}