package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long userId, BookingNewDto dto);

    BookingDto changeStatus(Long userId, Long bookingId, Boolean status);

    BookingDto getBookingById(Long userId, Long bookingId);

    Collection<Booking> getAllBookingsByUserId(Long userId, String state);

    Collection<Booking> getBookingsForAllItemsByUserId(Long userId, String state);

    Booking findLastBooking(Item item);

    Booking findNextBooking(Item item);

}
