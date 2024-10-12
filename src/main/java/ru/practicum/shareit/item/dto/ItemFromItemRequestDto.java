package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemFromItemRequestDto {

    private long id;
    private String name;
    private long ownerId;
}
