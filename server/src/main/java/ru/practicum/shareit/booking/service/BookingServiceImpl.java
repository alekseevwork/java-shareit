package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userService;
    private final ItemRepository itemService;

    public BookingServiceImpl(BookingRepository repository, UserRepository userService, ItemRepository itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public Booking create(Long userId, BookingNewDto dto) {
        log.info("create Booking - {}", dto);
        if (dto.getItemId() == null) {
            throw new NotFoundException("Not item id.");
        }
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        Item item = itemService.findItemWithOwnerById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item by id: " + dto.getItemId() + " not found"));

        if (!item.getAvailable()) {
            throw new ValidationException("Item by id - " + item.getId() + " not available.");
        }

        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new ValidationException("Start date is before end date.");
        }
        Booking booking = BookingMapper.toBooking(dto);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(StatusBooking.WAITING);

        return repository.save(booking);
    }

    @Override
    @Transactional
    public Booking changeStatus(Long userId, Long bookingId, Boolean status) {
        log.info("changeStatus Booking - {}", status);
        Booking booking = repository.findByIdFetchItemAndFetchUser(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking by id: " + bookingId + " not found"));
        if (booking.getItem().getOwner().getId() == userId) {
            changeStatusApprovedOrRejected(booking, status, userId);
        } else {
            throw new ValidationException("User by id - " + userId + " not owner");
        }
        return repository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        log.info("getBookingById - {}", bookingId);
        Booking booking = repository.findByIdFetchItemAndFetchUser(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking by id: " + bookingId + " not found"));
        if (userId.equals(booking.getBooker().getId())
                || userId.equals(booking.getItem().getOwner().getId())) {
            return booking;
        } else {
            throw new ValidationException("User by id - " + userId + " not owner");
        }
    }

    @Override
    public List<Booking> getAllBookingsByUserId(Long userId, String state) {
        log.info("getAllBookingsByUserId by state - {}", state);
        userService.findById(userId).orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        List<Booking> bookings;
        switch (state) {
            case "ALL" -> {
                bookings = repository.findAllByIdFetchItemAndFetchUser(userId);
            }
            case "CURRENT" -> {
                bookings = repository.findAllBookingByCurrentDate(userId, LocalDateTime.now());
            }
            case "PAST" -> {
                bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "WAITING" -> {
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING);
            }
            case "REJECTED" -> {
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED);
            }
            default -> throw new ValidationException("status - " + state + " is not supported");
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingsForAllItemsByUserId(Long userId, String state) {
        log.info("getBookingsAllItemsByUserId by state - {}", state);
        userService.findById(userId).orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        List<Booking> bookings;
        switch (state) {
            case "ALL" -> {
                bookings = repository.findAllByItemOwnerIdOrderByStartDesc(userId);
            }
            case "CURRENT" -> {
                bookings = repository.findAllByBookingItemsCurrentDate(userId, LocalDateTime.now());
            }
            case "PAST" -> {
                bookings = repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                bookings = repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "WAITING" -> {
                bookings = repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING);
            }
            case "REJECTED" -> {
                bookings = repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED);
            }
            default -> throw new ValidationException("status - " + state + " is not supported");
        }
        return bookings;
    }

    private void changeStatusApprovedOrRejected(Booking booking, Boolean state, Long userId) {
        if (state) {
            booking.setStatus(StatusBooking.APPROVED);
        } else if (userId.equals(booking.getBooker().getId())) {
            booking.setStatus(StatusBooking.CANCELED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
    }

    @Override
    public Booking findLastBooking(Long userId) {
        return repository
                .findFirstByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                .orElse(null);
    }

    @Override
    public Booking findNextBooking(Long userId) {
        return repository
                .findFirstByItemOwnerIdAndStartAfterOrderByStart(userId, LocalDateTime.now())
                .orElse(null);
    }
}
