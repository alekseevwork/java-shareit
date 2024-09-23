package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
@Builder
public class ItemShortDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;

    Booking lastBooking;
    Booking nextBooking;

    List<CommentDto> comments;
}
