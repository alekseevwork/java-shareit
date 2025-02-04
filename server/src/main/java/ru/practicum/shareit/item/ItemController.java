package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final BookingService bookingService;

    public ItemController(ItemService itemService, BookingService bookingService) {
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET /items/itemId: getItemsById - {}, user id - {}", itemId, userId);
        return ItemMapper.toDto(itemService.getItemsById(itemId), bookingService, itemService, userId);
    }

    @GetMapping
    public Collection<ItemShortDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items: getItemsByUserId by user id - {}", userId);
        return ItemMapper.toShortDto(itemService.getItemsByUserId(userId), bookingService, itemService, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("GET /items/search: getItemsByText - {}", text);
        return ItemMapper.toDto(itemService.getItemsByText(text), bookingService, itemService, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto item) {
        log.info("POST /items: create items {} - where owner {}", item, userId);
        return ItemMapper.toDto(itemService.create(userId, item), bookingService, itemService, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto item) {
        log.info("PATCH /items/id: update item by id - {}, by owner id - {}", itemId, userId);
        return ItemMapper.toDto(itemService.update(userId, itemId, item), bookingService, itemService, userId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("POST /items/id/comment: addComment - {} for item by id - {} - where owner {}",
                commentDto, itemId, userId);
        return CommentMapper.toDto(itemService.addComment(userId, itemId, commentDto));
    }
}
