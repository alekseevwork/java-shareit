package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    long id;
    String description;
    User requestor;
    LocalDateTime created;

    public ItemRequestDto(long id, String description, User requestor, LocalDateTime created) {
    }
}
