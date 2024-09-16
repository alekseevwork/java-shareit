package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepositoryMemory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryMemoryImpl implements ItemRepositoryMemory {

    Map<Long, Item> items = new HashMap<>();
    Long id = 1L;

    UserRepositoryMemory userRepository;

    public ItemRepositoryMemoryImpl(UserRepositoryMemory userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Item> getItemsById(Long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public Collection<Item> getItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId).toList();
    }

    @Override
    public Collection<Item> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().contains(text))
                .toList();
    }

    @Override
    public Item create(Long userId, Item item) {
        isUserExist(userId);

        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item by id - " + itemId + " not found.");
        }
        Item oldItem = items.get(itemId);
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("User by id - " + userId + " not owner this item.");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, oldItem);
        return oldItem;
    }

    public void isUserExist(Long userId) {
        if (userRepository.findUserById(userId).isEmpty()) {
            throw new NotFoundException("User by id - " + userId + " not found");
        }
    }
}
