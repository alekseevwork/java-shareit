package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private User user;

    @BeforeEach
    void before() {
        bookingRepository.deleteAll();
        user = new User(1L, "user", "user@example.com");
        userService.create(UserMapper.toDto(user));

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item);

    }

    @Test
    void getItemsById_whenInvoked_thenReturnItem() {
        Item actualItem = itemService.getItemsById(1L);

        assertEquals(item.getName(), actualItem.getName());
        assertEquals(item.getDescription(), actualItem.getDescription());
        assertEquals(item.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void getItemsById_whenItemIdNotFound_thenThrowNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.getItemsById(100L));

        assertEquals("Item by id: 100 not found", exception.getMessage());
    }

    @Test
    void getItemsByUserId_whenInvoked_thenReturnListItems() {
        Item item2 = Item.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item2);

        Collection<Item> actualItems = itemService.getItemsByUserId(1L);

        assertEquals(2, actualItems.size());
    }

    @Test
    void getItemsByText_whenInvoked_thenReturnListItems() {
        Item item2 = Item.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item2);

        Collection<Item> actualItems = itemService.getItemsByText("item");

        assertEquals(2, actualItems.size());
    }

    @Test
    void create_whenInvoked_thenReturnSavedItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();

        Item actualItem = itemService.create(1L, itemDto);

        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void create_whenInvalidRequest_thenThrowValidationException() {
        ItemDto itemDto = ItemDto.builder()
                .available(true)
                .build();
        Exception exception = assertThrows(ValidationException.class, () -> itemService.create(user.getId(), itemDto));

        assertEquals("Name or Description is null.", exception.getMessage());
    }

    @Test
    void update_whenInvoked_thenReturnSavedItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("new item")
                .description("new description")
                .available(false)
                .build();

        Item actualItem = itemService.update(user.getId(), item.getId(), itemDto);

        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void update_whenUserNotOwner_thenThrowValidationException() {
        User user2 = userService.create(UserMapper.toDto(
                new User(2L, "user2", "user2@example.com")
        ));
        ItemDto itemDto = ItemDto.builder()
                .name("new item")
                .description("new description")
                .available(false)
                .build();

        Exception exception = assertThrows(
                ValidationException.class, () -> itemService.update(user2.getId(), item.getId(), itemDto));
        assertEquals("No access User by id - 2", exception.getMessage());
    }

    @Test
    void addComment_whenInvoked_thenRerunSavedComment() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusHours(1))
                .status(StatusBooking.APPROVED)
                .build();

        bookingRepository.save(booking);

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        Comment actualComment = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertEquals(commentDto.getText(), actualComment.getText());
        assertEquals(item, actualComment.getItem());
        assertEquals(user, actualComment.getAuthor());
    }

    @Test
    void addComment_whenBookingNotFound_thenThrowValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        Exception exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertEquals("Booking not found.", exception.getMessage());
    }

    @Test
    void getComments_whenInvoked_thenReturnListComments() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .item(item)
                .author(user)
                .text("comment1")
                .created(LocalDateTime.now().minusDays(1))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .item(item)
                .author(user)
                .text("comment2")
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<CommentDto> actualComments = itemService.getComments(item);

        assertEquals(2, actualComments.size());
        assertEquals(comment2.getText(), actualComments.get(0).getText());
        assertEquals(comment1.getText(), actualComments.get(1).getText());
    }
}

