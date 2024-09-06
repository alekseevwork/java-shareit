package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItemsById(Long itemId);

    Collection<ItemShortDto> getItemsByUser(Long userId);

    Collection<ItemDto> getItemsByText(String text);

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);
}
