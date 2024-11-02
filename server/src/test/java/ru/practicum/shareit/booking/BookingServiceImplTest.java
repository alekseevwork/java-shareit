package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void before() {
        user = new User(1L, "User  ", "user@example.com");
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(user)
                .item(item)
                .status(StatusBooking.WAITING)
                .build();
    }

    @Test
    void createBooking_whenInvoked_thenReturnBooking() {
        BookingNewDto dto = new BookingNewDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findItemWithOwnerById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking actualBooking = bookingService.create(1L, dto);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
        assertEquals(booking.getBooker().getId(), actualBooking.getBooker().getId());
        assertEquals(StatusBooking.WAITING, actualBooking.getStatus());
    }

    @Test
    void createBooking_whenInvalidItemId_thenThrowsNotFoundException() {
        BookingNewDto dto = new BookingNewDto(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void createBooking_whenInvalidUserId_thenThrowsNotFoundException() {
        BookingNewDto dto = new BookingNewDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, dto));
    }

    @Test
    void createBooking_whenInvalidItemAvailability_thenThrowsValidationException() {
        item.setAvailable(false);
        when(itemRepository.findItemWithOwnerById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        BookingNewDto dto = new BookingNewDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void createBooking_whenInvalidDates_thenThrowsValidationException() {
        when(itemRepository.findItemWithOwnerById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        BookingNewDto dto = new BookingNewDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now());

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void changeStatus_whenInvoked_thenReturnBooking() {
        Boolean status = true;
        when(bookingRepository.findByIdFetchItemAndFetchUser(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking actualBooking = bookingService.changeStatus(1L, 1L, status);

        assertNotNull(actualBooking);
        assertEquals(StatusBooking.APPROVED, actualBooking.getStatus());
    }

    @Test
    void changeStatus_whenInvalidBookingId_thenThrowsNotFoundException() {
        Boolean status = true;

        assertThrows(NotFoundException.class, () -> bookingService.changeStatus(1L, 2L, status));
    }

    @Test
    void changeStatus_whenInvalidUserId_thenThrowsValidationException() {
        Boolean status = true;
        when(bookingRepository.findByIdFetchItemAndFetchUser(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.changeStatus(2L, 1L, status));
    }

    @Test
    void getBookingById_whenInvoked_thenReturnBooking() {
        when(bookingRepository.findByIdFetchItemAndFetchUser(anyLong())).thenReturn(Optional.of(booking));
        Booking actualBooking = bookingService.getBookingById(1L, 1L);

        assertNotNull(actualBooking);
        assertEquals(booking.getId(), actualBooking.getId());
    }

    @Test
    void getBookingById_whenInvalidBookingId_thenThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 2L));
    }

    @Test
    void getBookingById_whenInvalidUserId_thenThrowsValidationException() {
        when(bookingRepository.findByIdFetchItemAndFetchUser(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(2L, 1L));
    }

    @Test
    void getAllBookingsByUserId_whenInvoked_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByIdFetchItemAndFetchUser(anyLong())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "ALL");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenStateCURRENT_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllBookingByCurrentDate(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "CURRENT");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenStatePAST_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "PAST");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenStateFUTURE_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "FUTURE");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenStateWAITING_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(StatusBooking.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "WAITING");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenStateREJECTED_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(StatusBooking.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getAllBookingsByUserId(1L, "REJECTED");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getAllBookingsByUserId_whenInvalidUserId_thenThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUserId(anyLong(), "ALL"));
    }

    @Test
    void getAllBookingsByUserId_whenInvalidState_thenThrowsValidationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> bookingService.getAllBookingsByUserId(1L, "INVALID_STATE"));
    }

    @Test
    void getBookingsForAllItemsByUserId_whenInvoked_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(anyLong(), "ALL");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenStateCURRENT_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookingItemsCurrentDate(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(1L, "CURRENT");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenStatePAST_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(1L, "PAST");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenStateFUTURE_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(1L, "FUTURE");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenStateWAITING_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(StatusBooking.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(1L, "WAITING");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenStateREJECTED_thenReturnListBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(StatusBooking.class)))
                .thenReturn(List.of(booking));

        List<Booking> actualBookings = bookingService.getBookingsForAllItemsByUserId(1L, "REJECTED");

        assertNotNull(actualBookings);
        assertEquals(1, actualBookings.size());
        assertEquals(booking.getId(), actualBookings.get(0).getId());
    }

    @Test
    void getBookingsForAllItemsByUserId_whenInvalidUserId_thenThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsForAllItemsByUserId(2L, "ALL"));
    }

    @Test
    void getBookingsForAllItemsByUserId_whenInvalidState_thenThrowsValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsForAllItemsByUserId(1L, "INVALID_STATE"));
    }

    @Test
    void findLastBooking_whenInvoked_thenReturnBooking() {
        when(bookingRepository.findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.findLastBooking(1L);

        assertNotNull(actualBooking);
        assertEquals(booking.getId(), actualBooking.getId());
    }

    @Test
    void findLastBooking_whenNoBookings_thenReturnNull() {
        when(bookingRepository.findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Booking actualBooking = bookingService.findLastBooking(1L);

        assertNull(actualBooking);
    }

    @Test
    void findNextBooking_whenInvoked_thenReturnBooking() {
        when(bookingRepository.findFirstByItemOwnerIdAndStartAfterOrderByStart(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Booking actualBooking = bookingService.findNextBooking(1L);

        assertNotNull(actualBooking);
        assertEquals(booking.getId(), actualBooking.getId());
    }

    @Test
    void findNextBooking_whenNoBookings_thenReturnNull() {
        when(bookingRepository.findFirstByItemOwnerIdAndStartAfterOrderByStart(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Booking actualBooking = bookingService.findNextBooking(1L);

        assertNull(actualBooking);
    }
}
