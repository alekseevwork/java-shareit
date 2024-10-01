package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto dto) {
        log.info("POST /requests: create ItemRequest - {}, by user id - {}", dto, userId);
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestAnswerDto> getAllItemRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests: getAllByUserId by user id - {}", userId);
        return requestService.getAllItemRequestByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestAnswerDto> getAllItemRequestButThisUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/all: getAllButUserId by user id - {}", userId);
        return requestService.getAllItemRequestButThisUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestAnswerDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable("requestId") Long requestId) {
        log.info("GET /requests/id: getById requestId - {}, by user id - {}", requestId, userId);
        return requestService.getById(requestId);
    }
}
