package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    Long owner;
    ItemRequest request;

    public boolean isAvailable() {
        return available;
    }
}
