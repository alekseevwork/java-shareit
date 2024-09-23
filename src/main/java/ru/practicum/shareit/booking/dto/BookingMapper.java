package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Collection<BookingDto> toBookingDto(Collection<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    public static Booking toBooking(BookingNewDto dto) {
        if (dto == null) {
            return null;
        }
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }
}
