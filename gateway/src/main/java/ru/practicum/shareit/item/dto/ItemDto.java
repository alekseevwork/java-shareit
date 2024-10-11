package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NonNull
    private Boolean available;

    private Long requestId;
}