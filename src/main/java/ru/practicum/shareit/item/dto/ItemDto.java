package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.beans.ConstructorProperties;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;

    public ItemDto(long id, String name, String description, boolean available, Long aLong) {
    }
}
