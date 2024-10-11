package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NonNull
    private Boolean available;

    private User owner;
    private Long requestId;

    private Booking lastBooking;
    private Booking nextBooking;

    private List<CommentDto> comments;
}
