package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET /items/itemId: getItemsById - {}, user id - {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items: getItemsByUserId by user id - {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam String text) {
        log.info("GET /items/search: getItemsByText - {}", text);
        return itemClient.getItemsByText(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated @RequestBody ItemDto itemDto) {
        log.info("POST /items: create items {} - where owner {}", itemDto, userId);
        System.out.println(itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/id: update item by id - {}, by owner id - {}", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Validated @RequestBody CommentDto commentDto) {
        log.info("POST /items/id/comment: addComment - {} for item by id - {} - where owner {}",
                commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
