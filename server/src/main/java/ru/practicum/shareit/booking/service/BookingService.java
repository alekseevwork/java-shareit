package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingNewDto;

import java.util.List;

public interface BookingService {

    Booking create(Long userId, BookingNewDto dto);

    Booking changeStatus(Long userId, Long bookingId, Boolean status);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUserId(Long userId, String state);

    List<Booking> getBookingsForAllItemsByUserId(Long userId, String state);

    Booking findLastBooking(Long userId);

    Booking findNextBooking(Long userId);
}
