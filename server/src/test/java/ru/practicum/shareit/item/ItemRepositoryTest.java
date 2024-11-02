package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequest request;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(new User(1L, "Name", "user@example.com"));
        userRepository.save(user);

        request = ItemRequest.builder()
                .description("description request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

        itemRequestRepository.save(request);

        item1 = Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .requestId(request.getId())
                .build();
        item2 = Item.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .owner(user)
                .requestId(request.getId())
                .build();
        itemRepository.saveAll(List.of(item1, item2));
    }

    @Test
    void findAllByOwnerId() {
        Collection<Item> actualItems = itemRepository.findAllByOwnerId(user.getId());

        assertEquals(2, actualItems.size());
        assertEquals(item1.getId(), actualItems.stream().findFirst().get().getId());
        assertEquals(item2.getId(), actualItems.stream().skip(1).findFirst().get().getId());
    }

    @Test
    void findItemWithOwnerById() {
        Optional<Item> actualItem = itemRepository.findItemWithOwnerById(item1.getId());

        assertTrue(actualItem.isPresent());
        assertEquals(item1.getOwner(), actualItem.get().getOwner());
    }

    @Test
    void findAllByNamePattern() {
        Collection<Item> actualItems = itemRepository.findAllByNamePattern("item");

        assertEquals(2, actualItems.size());
        assertEquals(item1.getId(), actualItems.stream().findFirst().get().getId());
        assertEquals(item2.getId(), actualItems.stream().skip(1).findFirst().get().getId());
    }

    @Test
    public void whenSearchByName_thenReturnItem() {
        Collection<Item> actualItems = itemRepository.findAllByNamePattern("item1");
        assertEquals(1, actualItems.size());
        assertThat(actualItems).contains(item1);
    }

    @Test
    public void whenSearchByDescription_thenReturnItem() {
        Collection<Item> actualItems = itemRepository.findAllByNamePattern("item2");
        assertEquals(1, actualItems.size());
        assertThat(actualItems).contains(item2);
    }

    @Test
    void findAllByRequestId() {
        List<Item> actualItems = itemRepository.findAllByRequestId(request.getId());

        assertEquals(2, actualItems.size());
        assertEquals(item1.getId(), actualItems.stream().findFirst().get().getId());
        assertEquals(item2.getId(), actualItems.stream().skip(1).findFirst().get().getId());
    }
}
