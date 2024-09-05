package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository("itemRepositoryMemory")
@RequiredArgsConstructor
public class ItemRepositoryMemory implements ItemRepository {
    HashMap<Long, Item> items = new HashMap<>();
    Long id = 1L;

    @Override
    public Optional<Item> getItemsById(Long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public Collection<Item> getItemsByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId)).toList();
    }

    @Override
    public Collection<Item> getItemsByText(String text) {
        if (text == null) {
            throw new ValidationException("Text searching cannot  by null");
        }
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().contains(text) || item.getDescription().contains(text))
                .toList();
    }

    @Override
    public Item create(Long userId, Item item) {
        item.setOwner(userId);
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw  new NotFoundException("Item by id - " + itemId + " not found.");
        }
        Item oldItem = items.get(itemId);
        if (!oldItem.getOwner().equals(userId)) {
            throw new ValidationException("User by id - " + userId + " not owner this item.");
        }
        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setAvailable(item.isAvailable());
        items.put(itemId, oldItem);
        return oldItem;
    }
}
