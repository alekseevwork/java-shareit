package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;


@Data
public class Item {

    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    boolean available;
    Long owner;
    ItemRequest request;
}
