package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Data
@Builder
public class ItemShortDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;

    private Booking lastBooking;
    private Booking nextBooking;

    private List<CommentDto> comments;
}
