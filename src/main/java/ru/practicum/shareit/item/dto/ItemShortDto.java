package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemShortDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
}
