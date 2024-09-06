package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItemsById(Long itemId);

    Collection<Item> getItemsByUser(Long userId);

    Collection<Item> getItemsByText(String text);

    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);
}
