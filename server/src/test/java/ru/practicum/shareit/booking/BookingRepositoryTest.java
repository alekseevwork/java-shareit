package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Booking booking1;
    private Booking booking2;
    private User user;
    private Item item;

    @BeforeEach
    void before() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

        LocalDateTime dateTime = LocalDateTime.now();
        user = userRepository.save(new User(1L, "User", "user@example.com"));
        item = itemRepository.save(Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build());
        booking1 = Booking.builder()
                .start(dateTime)
                .end(dateTime.plusDays(1))
                .booker(user)
                .item(item)
                .status(StatusBooking.WAITING)
                .build();
        booking2 = Booking.builder()
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(2))
                .booker(user)
                .item(item)
                .status(StatusBooking.REJECTED)
                .build();
        bookingRepository.saveAll(List.of(booking1, booking2));
    }

    @Test
    void findByIdFetchItemAndFetchUser_whenInvoked_thenReturnBooking1() {
        Optional<Booking> actualBooking = bookingRepository.findByIdFetchItemAndFetchUser(booking1.getId());

        assertTrue(actualBooking.isPresent());
        assertEquals(booking1.getId(), actualBooking.get().getId());
        assertEquals(booking1.getItem().getId(), actualBooking.get().getItem().getId());
        assertEquals(booking1.getBooker().getId(), actualBooking.get().getBooker().getId());
    }

    @Test
    void findAllByIdFetchItemAndFetchUser_whenBookerIdIn2Bookings_thenReturnListBookingsSizeIn2() {
        List<Booking> actualBookings = bookingRepository.findAllByIdFetchItemAndFetchUser(user.getId());

        assertEquals(2, actualBookings.size());
        assertEquals(booking2.getId(), actualBookings.get(0).getId());
        assertEquals(booking1.getId(), actualBookings.get(1).getId());
        assertEquals(user, actualBookings.get(0).getBooker());
        assertEquals(user, actualBookings.get(1).getBooker());
    }

    @Test
    void findAllBookingByCurrentDate_whenDateNow_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository.findAllBookingByCurrentDate(user.getId(), LocalDateTime.now());

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
        assertEquals(item, actualBookings.get(0).getItem());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc_whenDatePlusDay_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now().plusDays(1));

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc_whenDateNow_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now());

        assertEquals(1, actualBookings.size());
        assertEquals(booking2.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc_whenStatusWAITING_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), StatusBooking.WAITING);

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc_whenOwnerIdIn2Bookers_thenReturnListBookingsSizeIn2() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(item.getOwner().getId());

        assertEquals(2, actualBookings.size());
        assertEquals(booking2.getId(), actualBookings.get(0).getId());
        assertEquals(booking1.getId(), actualBookings.get(1).getId());
    }

    @Test
    void findAllByBookingItemsCurrentDate_whenInvoked__thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByBookingItemsCurrentDate(user.getId(), LocalDateTime.now());

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc_whenInvoked_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now().plusDays(1));

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc_whenInvoked_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now());

        assertEquals(1, actualBookings.size());
        assertEquals(booking2.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc_whenNotOwner_thenReturnListEmpty() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(100L, LocalDateTime.now());
        assertTrue(actualBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc_whenStatusWAITING_thenReturnListByOneBooker() {
        List<Booking> actualBookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), StatusBooking.WAITING);

        assertEquals(1, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
    }

    @Test
    void findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc_whenDatePlusDay_thenReturnBooking1() {
        Optional<Booking> actualBooking = bookingRepository
                .findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now().plusDays(1));

        assertTrue(actualBooking.isPresent());
        assertEquals(booking1.getId(), actualBooking.get().getId());
    }

    @Test
    void findFirstByItemOwnerIdAndStartAfterOrderByStart_whenInvoked_thenReturnBooking2() {
        Optional<Booking> actualBooking = bookingRepository
                .findFirstByItemOwnerIdAndStartAfterOrderByStart(user.getId(), LocalDateTime.now());

        assertTrue(actualBooking.isPresent());
        assertEquals(booking2.getId(), actualBooking.get().getId());
    }

    @Test
    void findByItemIdAndBookerIdAndEndBefore_whenInvoked_thenReturnBooking1() {
        Optional<Booking> actualBooking = bookingRepository
                .findByItemIdAndBookerIdAndEndBefore(item.getId(), user.getId(), LocalDateTime.now().plusDays(1));

        assertTrue(actualBooking.isPresent());
        assertEquals(booking1.getId(), actualBooking.get().getId());
    }
}