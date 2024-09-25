package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.id = :id")
    Optional<Booking> findByIdFetchItemAndFetchUser(Long id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    Collection<Booking> findAllByIdFetchItemAndFetchUser(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.start <= :time AND b.end >= :time ORDER BY b.start DESC")
    Collection<Booking> findAllBookingByCurrentDate(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.end <= :time ORDER BY b.start DESC")
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.start >= :time ORDER BY b.start DESC")
    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :ownerId ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.start <= :time AND b.end >= :time ORDER BY b.start DESC")
    Collection<Booking> findAllByBookingItemsCurrentDate(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.end <= :time ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.start >= :time ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, StatusBooking status);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime time);

    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);
}

