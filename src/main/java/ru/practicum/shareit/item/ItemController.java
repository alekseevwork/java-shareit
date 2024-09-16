package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(@Qualifier("itemServiceDb") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemsById(@PathVariable Long itemId) {
        log.info("GET /items/itemId: getItemsById - {}", itemId);
        return itemService.getItemsById(itemId);
    }

    @GetMapping
    public Collection<ItemShortDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items: getItemsByUserId by user id - {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByText(@RequestParam String text) {
        log.info("GET /items/search: getItemsByText - {}", text);
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated @RequestBody ItemDto item) {
        log.info("POST /items: create items {} - where owner {}", item, userId);
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto item) {
        log.info("PATCH /items/id: update item by id - {}, by owner id - {}", itemId, userId);
        return itemService.update(userId, itemId, item);
    }
}
