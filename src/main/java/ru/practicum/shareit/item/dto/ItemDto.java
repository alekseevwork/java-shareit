package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Data
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    boolean available;
}
