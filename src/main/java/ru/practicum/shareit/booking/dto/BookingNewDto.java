package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingNewDto {

    Long itemId;
    @NonNull
    LocalDateTime start;
    @NonNull
    LocalDateTime end;
}
