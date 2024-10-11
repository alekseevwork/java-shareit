package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    ObjectMapper objectMapper;

    private User user;
    private Item item;
    private Booking booking;
    private BookingNewDto newDto;

    @BeforeEach
    void before() {
        user = new User(1L, "name", "email@email.ru");
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        newDto = BookingNewDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(StatusBooking.APPROVED)
                .build();
    }

    @SneakyThrows
    @Test
    void createBooking_whenInvoked_thenStatusOkAndReturnBooking() {
        when(bookingService.create(1L, newDto)).thenReturn(booking);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Booking actualBooking = objectMapper.readValue(result, Booking.class);
        assertEquals(booking.getItem(), actualBooking.getItem());
        assertEquals(booking.getStart(), actualBooking.getStart());
        assertEquals(booking.getEnd(), actualBooking.getEnd());
    }

    @SneakyThrows
    @Test
    void changeStatus_whenInvoked_thenStatusOkAndReturnBooking() {
        when(bookingService.changeStatus(1L, 1L, true)).thenReturn(booking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Booking actualBooking = objectMapper.readValue(result, Booking.class);
        assertEquals(StatusBooking.APPROVED, actualBooking.getStatus());
    }

    @SneakyThrows
    @Test
    void getBookingById_whenInvoked_thenStatusOkAndReturnBooking() {
        when(bookingService.getBookingById(1L, 1L)).thenReturn(booking);

        String result = mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Booking actualBooking = objectMapper.readValue(result, Booking.class);
        assertEquals(booking.getId(), actualBooking.getId());
        assertEquals(booking.getItem(), actualBooking.getItem());
        assertEquals(booking.getStart(), actualBooking.getStart());
        assertEquals(booking.getEnd(), actualBooking.getEnd());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUserId_whenInvoked_thenStatusOkAndReturnListBookings() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        List<Booking> bookings = List.of(booking, booking2);

        when(bookingService.getAllBookingsByUserId(user.getId(), "ALL")).thenReturn(bookings);


        MvcResult result = mockMvc.perform(get("/bookings", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Booking> actualBookings = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        assertEquals(2, actualBookings.size());
        assertEquals(booking.getItem(), actualBookings.stream().findFirst().get().getItem());
        assertEquals(booking2.getItem(), actualBookings.stream().skip(1).findFirst().get().getItem());
    }

    @SneakyThrows
    @Test
    void getBookingsAllItemsByUserId_whenUserId1_thenStatusOkAndReturnListBookings() {

        Booking booking2 = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        List<Booking> bookings = List.of(booking, booking2);

        when(bookingService.getBookingsForAllItemsByUserId(user.getId(), "ALL")).thenReturn(bookings);

        MvcResult result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andReturn();

        List<Booking> actualBookings = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        assertEquals(2, actualBookings.size());
        assertEquals(booking.getItem(), actualBookings.stream().findFirst().get().getItem());
        assertEquals(booking2.getItem(), actualBookings.stream().skip(1).findFirst().get().getItem());
    }
}
