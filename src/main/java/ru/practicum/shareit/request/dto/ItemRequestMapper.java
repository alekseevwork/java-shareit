package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest item) {
        if (item == null) {
            return null;
        }
        return new ItemRequestDto(
                item.getId(),
                item.getDescription(),
                item.getRequestor(),
                item.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .build();
    }

    public static ItemRequestAnswerDto toAnswerDto(ItemRequest itemRequest, List<Item> items) {
        if (itemRequest == null) {
            return null;
        }
        return new ItemRequestAnswerDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(ItemMapper::fromItemRequestDto).toList()
        );
    }
}
