package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {

    Booking create(Long userId, BookingNewDto dto);

    Booking changeStatus(Long userId, Long bookingId, Boolean status);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUserId(Long userId, String state);

    List<Booking> getBookingsForAllItemsByUserId(Long userId, String state);

    Booking findLastBooking(Item item);

    Booking findNextBooking(Item item);

}
