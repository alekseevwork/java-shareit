package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemRequestServiceImplTest {

    @MockBean
    private ItemRequestRepository requestRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void before() {
        user = new User(1L, "User", "user@example.com");
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .build();
    }

    @Test
    void create_whenSuccess_thenReturnSavedRequest() {
        ItemRequestDto requestDtoSaved = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto actualRequest = itemRequestService.create(user.getId(), itemRequestDto);

        assertEquals(requestDtoSaved.getRequestor(), actualRequest.getRequestor());
        verify(requestRepository, times(1)).save(itemRequest);
    }

    @Test
    void addNewRequest_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userService.findUserById(user.getId())).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemRequestService.create(user.getId(), itemRequestDto));
    }

    @Test
    void getAllItemRequestByUserId_whenUserId1_thenReturnListRequests() {
        List<ItemRequest> itemRequests = List.of(
                ItemRequest.builder()
                        .id(1L)
                        .description("description")
                        .build(),
                ItemRequest.builder()
                        .id(2L)
                        .description("description")
                        .build());
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(itemRequests);

        List<ItemRequestAnswerDto> actualDtos = itemRequestService.getAllItemRequestByUserId(1L);

        assertEquals(2, actualDtos.size());
    }

    @Test
    void getAllItemRequestExceptUserId_whenUserId1_thenReturnListRequests() {
        List<ItemRequest> itemRequests = List.of(
                ItemRequest.builder()
                        .id(1L)
                        .description("description")
                        .build(),
                ItemRequest.builder()
                        .id(2L)
                        .description("description")
                        .build());
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L)).thenReturn(itemRequests);

        List<ItemRequestAnswerDto> actualDtos = itemRequestService.getAllItemRequestExceptUserId(1L);

        assertEquals(2, actualDtos.size());
    }

    @Test
    void getById_whenUserId1_thenReturnRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        ItemRequestAnswerDto actualDto = itemRequestService.getById(1L);

        assertNotNull(actualDto);
        assertEquals(itemRequest.getDescription(), actualDto.getDescription());
    }

    @Test
    void getById_whenUserNotFound_thenThrowNotFoundException() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L));
    }
}