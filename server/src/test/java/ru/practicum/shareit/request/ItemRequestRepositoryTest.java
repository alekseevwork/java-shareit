package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private User user1;
    private User user2;

    @BeforeEach
    void before() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        user1 = userRepository.save(new User(1L, "User", "user@example.com"));
        user2 = userRepository.save(new User(2L, "User2", "user2@example.com"));


        itemRequest1 = ItemRequest.builder()
                .description("Request 1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build();
        itemRequest2 = ItemRequest.builder()
                .description("Request 2")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();

        itemRequestRepository.saveAll(List.of(itemRequest1, itemRequest2));
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenInvoked_thenReturnRequestByRequestorUser1() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest1, itemRequests.get(0));
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenInvoked_thenReturnRequestByRequestorUser2() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user2.getId());

        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest2, itemRequests.get(0));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_whenInvoked_thenReturnRequests() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(user1.getId(), PageRequest.of(0, 2));
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest2, itemRequests.get(0));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_whenNotRequesterId_thenReturnEmpty() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(220L);
        assertTrue(itemRequests.isEmpty());
    }
}
