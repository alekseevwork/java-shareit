package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.id = :bookerId")
    Optional<Booking> findByIdFetchItemAndFetchUser(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByIdFetchItemAndFetchUser(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.start <= :time AND b.end >= :time ORDER BY b.start DESC")
    List<Booking> findAllBookingByCurrentDate(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.end <= :time ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.start >= :time ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.booker.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.start <= :time AND b.end >= :time ORDER BY b.start DESC")
    List<Booking> findAllByBookingItemsCurrentDate(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.end <= :time ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.start >= :time ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, StatusBooking status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId AND b.end < :time ORDER BY b.start DESC")
    Optional<Booking> findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.id = :ownerId AND b.start > :time ORDER BY b.start DESC")
    Optional<Booking> findFirstByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :time ORDER BY b.start DESC")
    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);
}
