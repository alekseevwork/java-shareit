package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void before() {
        itemDto = new ItemDto(1L, "Item", "description", true, 1L);
        commentDto = new CommentDto(1L, "Comment");
    }

    @Test
    void getItemById_whenValidId_thenStatusOkAndReturnItem() throws Exception {
        when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(itemDto));

        MvcResult result = mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String body = "{\"id\":1,\"name\":\"Item\",\"description\":\"description\",\"available\":true,\"requestId\":1}";
        assertEquals(body, responseBody);
    }

    @Test
    void getItemByI_whenInvalidId_thenStatusNotFound() throws Exception {
        when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void getItemsByUserId_whenUserIdFound_thenStatusOkAndReturnListItems() throws Exception {
        when(itemClient.getItems(anyLong())).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String body = "[{\"id\":1,\"name\":\"Item\",\"description\":\"description\",\"available\":true,\"requestId\":1}]";
        assertEquals(body, responseBody);
    }

    @Test
    void getItemsByText_whenFoundText_thenStatusOkAndReturnListItem() throws Exception {
        when(itemClient.getItemsByText(anyLong(), any(String.class))).thenReturn(ResponseEntity.ok(List.of(itemDto)));

        MvcResult result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "description"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String body = "[{\"id\":1,\"name\":\"Item\",\"description\":\"description\",\"available\":true,\"requestId\":1}]";
        assertEquals(body, responseBody);
    }

    @Test
    void create_whenValidRequest_thenStatusOkAndReturnItem() throws Exception {
        when(itemClient.create(anyLong(), any(ItemDto.class))).thenReturn(ResponseEntity.ok(itemDto));

        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String body = "{\"id\":1,\"name\":\"Item\",\"description\":\"description\",\"available\":true,\"requestId\":1}";
        assertEquals(body, responseBody);
    }

    @Test
    void create_whenInvalidItem_thenReturnStatusBadRequest() throws Exception {
        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.badRequest().build());
        itemDto = new ItemDto(2L, "", "description", true, 2L);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(itemClient, never()).update(1L, 1L, itemDto);
    }

    @Test
    void update_whenValidRequest_thenStatusOkAndReturnItem() throws Exception {
        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(ResponseEntity.ok(itemDto));

        MvcResult result = mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"name\":\"Item\",\"description\":\"description\",\"available\":true,\"requestId\":1}", responseBody);
    }

    @Test
    void update_whenInvalidItemId_thenStatusNotFound() throws Exception {
        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/items/2")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(itemClient, never()).update(1L, 1L, itemDto);
    }

    @Test
    void update_whenInvalidItem_thenStatusBadRequest() throws Exception {
        itemDto = new ItemDto(1L, "", "description", false, 1L);
        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void addComment_whenValidComment_thenStatusOkAndReturnComment() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(commentDto));

        MvcResult result = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"text\":\"Comment\"}", responseBody);
    }

    @Test
    void addComment_whenInvalidItemId_thenStatusNotFound() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(post("/items/2/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(itemClient, never()).addComment(1L, 1L, commentDto);
    }

    @Test
    void addComment_whenInvalidComment_thenStatusBadRequest() throws Exception {
        commentDto = new CommentDto(1L, "");
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(itemClient, never()).addComment(1L, 1L, commentDto);
    }
}
