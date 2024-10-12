package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookItemRequestDto bookingDto;

    @BeforeEach
    void before() {
        bookingDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    void getBookings_whenInvoked_thenStatusOkAndReturnListBookings() throws Exception {
        LocalDateTime data = LocalDateTime.now();
        bookingDto = new BookItemRequestDto(1L, data, data.plusDays(1));
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void getBookings_whenInvalidState_thenStatusBadRequest() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(bookingClient, never()).getBookings(1L, BookingState.ALL, 1, 1);
    }

    @Test
    void getBookings_whenInvalidFrom_thenStatusBadRequest() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(bookingClient, never()).getBooking(1L, 1L);
    }

    @Test
    void getBookings_whenInvalidSize_thenStatusBadRequest() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(bookingClient, never()).getBooking(1L, 1L);
    }

    @Test
    void bookItem_whenValidRequest_thenStatusOkAndReturnBooking() throws Exception {
        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((objectMapper.writeValueAsString(bookingDto))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void bookItem_whenInvalidRequest_thenStatusBadRequest() throws Exception {
        bookingDto = new BookItemRequestDto(1L, null, LocalDateTime.now());
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(bookingClient, never()).bookItem(1L, bookingDto);
    }


    @Test
    void getBooking_whenValidRequest_thenStatusOkAndReturnBooking() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void changeStatus_whenValidRequest_thenStatusOkAndReturnBooking() throws Exception {
        when(bookingClient.changeStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        verify(bookingClient, times(1)).changeStatus(1L, 1L, true);
    }

    @Test
    void changeStatus_whenInvalidBookingId_thenStatusBadRequest() throws Exception {
        when(bookingClient.changeStatus(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(patch("/bookings/-1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andReturn();
        verify(bookingClient, never()).changeStatus(1L, 1L, true);
    }

    @Test
    void getBookingsAllItemsByUserId_whenValidRequest_thenStatusOkAndReturnBooking() throws Exception {
        when(bookingClient.getBookingsOwner(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(List.of(bookingDto)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        verify(bookingClient, times(1)).getBookingsOwner(1L, BookingState.ALL);
    }

    @Test
    void getBookingsAllItemsByUserId_whenInvalidState_thenStatusBadRequest() throws Exception {
        when(bookingClient.getBookingsOwner(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "INVALID"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
