package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Item getItemsById(Long itemId);

    Collection<Item> getItemsByUserId(Long userId);

    Collection<Item> getItemsByText(String text);

    Item create(Long userId, ItemDto item);

    Item update(Long userId, Long itemId, ItemDto item);

    Comment addComment(Long userId, Long itemId, CommentDto commentDto);

    List<CommentDto> getComments(Item item);
}
