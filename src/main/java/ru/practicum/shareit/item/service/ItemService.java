package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto getItemsById(Long itemId);
    Collection<ItemDto> getItemsByUser(Long userId);
    Collection<ItemDto> getItemsByText(String text);
    ItemDto create(Long userId, Item item);
    ItemDto update(Long userId, Long itemId, Item item);
}
