package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto requestDto);

    List<ItemRequestAnswerDto> getAllItemRequestByUserId(Long userId);

    List<ItemRequestAnswerDto> getAllItemRequestExceptUserId(Long userId);

    ItemRequestAnswerDto getById(Long itemRequestId);
}
