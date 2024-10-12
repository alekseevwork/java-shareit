package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    ObjectMapper objectMapper;

    private RequestDto requestDto;

    @BeforeEach
    void before() {
        requestDto = new RequestDto(1L, "description");
    }

    @Test
    void create_whenValidRequest_thenStatusOkAndReturnRequest() throws Exception {
        when(requestClient.create(anyLong(), any(RequestDto.class))).thenReturn(ResponseEntity.ok(requestDto));

        MvcResult result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"description\":\"description\"}", responseBody);
    }

    @Test
    void create_whenInvalidRequest_thenStatusBadRequest() throws Exception {
        requestDto = new RequestDto(1L, "");
        when(requestClient.create(anyLong(), any(RequestDto.class))).thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(requestClient, never()).create(1L, requestDto);
    }

    @Test
    void getAllItemRequestByUserId_whenInvoked_thenStatusOkAndReturnListRequests() throws Exception {
        when(requestClient.getRequestsByUserId(anyLong())).thenReturn(ResponseEntity.ok(List.of(requestDto)));

        MvcResult result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"description\":\"description\"}]", responseBody);
    }

    @Test
    void getAllItemRequestExceptUserId_whenUserIdValid_thenStatusOkAndReturnListEmpty() throws Exception {
        when(requestClient.getRequestsExceptUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        MvcResult result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("[]", responseBody);
    }

    @Test
    void getAllItemRequestExceptUserId_whenSizeNegative_thenStatusBadRequest() throws Exception {
        when(requestClient.getRequestsExceptUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("size", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        verify(requestClient, never()).getRequestsExceptUserId(1L, 0, -1);
    }

    @Test
    void getAllItemRequestExceptUserId_whenFromNegative_thenStatusBadRequest() throws Exception {
        when(requestClient.getRequestsExceptUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        verify(requestClient, never()).getRequestsExceptUserId(1L, -1, 10);
    }

    @Test
    void getById_whenUserIdValid_thenStatusOkAndReturnRequest() throws Exception {
        when(requestClient.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(requestDto));

        MvcResult result = mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"description\":\"description\"}", responseBody);
    }

    @Test
    void getById_whenInvalidId_thenStatusNotFound() throws Exception {
        when(requestClient.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
