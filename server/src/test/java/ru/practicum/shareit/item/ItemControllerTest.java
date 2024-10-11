package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private List<Item> items;
    private CommentDto commentDto;

    @BeforeEach
    void before() {
        user = new User(1L, "name", "email@email.ru");
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        items = List.of(
                item,
                Item.builder()
                        .id(2L)
                        .name("item2")
                        .description("description2")
                        .available(true)
                        .owner(user)
                        .build());
        commentDto = CommentDto.builder()
                .text("comment")
                .build();
    }

    @SneakyThrows
    @Test
    void getItemById_whenInvoked_thenReturnItem() {
        when(itemService.getItemsById(1L)).thenReturn(item);

        MvcResult result = mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn();

        ItemDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(item.getName(), actualDto.getName());
        assertEquals(item.getDescription(), actualDto.getDescription());
        assertEquals(item.getAvailable(), actualDto.getAvailable());
    }

    @SneakyThrows
    @Test
    void getItemsByUserId_whenInvoked_thenReturnListItems() {
        when(itemService.getItemsByUserId(1L)).thenReturn(items);

        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn();

        List<ItemShortDto> actualDtos = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(2, actualDtos.size());
    }

    @SneakyThrows
    @Test
    void getItemsByText_whenInvoked_thenReturnListItems() {
        when(itemService.getItemsByText("text")).thenReturn(items);

        MvcResult result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andReturn();

        List<ItemDto> actualDtos = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(2, actualDtos.size());
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnItem() {
        when(itemService.create(1L, itemDto)).thenReturn(item);

        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn();

        ItemDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(item.getName(), actualDto.getName());
        assertEquals(item.getDescription(), actualDto.getDescription());
        assertEquals(item.getAvailable(), actualDto.getAvailable());
    }

    @SneakyThrows
    @Test
    void update_whenInvoked_thenReturnItem() {
        when(itemService.update(1L, 1L, itemDto)).thenReturn(item);

        MvcResult result = mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn();

        ItemDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(item.getName(), actualDto.getName());
        assertEquals(item.getDescription(), actualDto.getDescription());
        assertEquals(item.getAvailable(), actualDto.getAvailable());
    }

    @SneakyThrows
    @Test
    void addComment_whenInvoked_thenReturnItem() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(user)
                .build();

        when(itemService.addComment(1L, 1L, commentDto)).thenReturn(comment);

        MvcResult result = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);
        assertEquals(comment.getText(), actualDto.getText());
    }

    @SneakyThrows
    @Test
    void addComment_whenInvalidItemId_thenThrowNotFoundExceptionAndStatusNotFound() {
        when(itemService.addComment(1L, 1L, commentDto)).thenThrow(new NotFoundException("Item not found"));

        MvcResult result = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String actualMessage = result.getResolvedException().getMessage();
        assertEquals("Item not found", actualMessage);
    }

    @SneakyThrows
    @Test
    void addComment_whenInvalidUserId_thenThrowNotFoundExceptionAndStatusNotFound() {
        when(itemService.addComment(1L, 1L, commentDto))
                .thenThrow(new NotFoundException("User not found"));

        MvcResult result = mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String actualMessage = result.getResolvedException().getMessage();
        assertEquals("User not found", actualMessage);
    }
}
