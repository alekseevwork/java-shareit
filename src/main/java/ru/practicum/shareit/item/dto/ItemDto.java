package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NonNull
    Boolean available;

    User owner;
    Long request;

    Booking lastBooking;
    Booking nextBooking;

    List<CommentDto> comments;
}
