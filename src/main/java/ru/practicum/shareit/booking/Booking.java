package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    Enum<StatusBooking> status;
}