package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceDb implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userService;
    private final ItemRepository itemService;

    @Autowired
    public BookingServiceDb(BookingRepository repository, UserRepository userService, ItemRepository itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingNewDto dto) {
        log.info("create Booking - {}", dto);
        if (dto.getItemId() == null) {
            throw new NotFoundException("Not item id.");
        }
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        Item item = itemService.findById(dto.getItemId())
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

        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto changeStatus(Long userId, Long bookingId, Boolean status) {
        log.info("changeStatus Booking - {}", status);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking by id: " + bookingId + " not found"));
        if (booking.getItem().getOwner().getId() == userId) {
            changeStatusApprovedOrRejected(booking, status);
        } else {
            throw new ValidationException("User by id - " + userId + " not owner");
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("getBookingById - {}", bookingId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking by id: " + bookingId + " not found"));
        if (userId.equals(booking.getBooker().getId())
                || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ValidationException("User by id - " + userId + " not owner");
        }
    }

    @Override
    public Collection<Booking> getAllBookingsByUserId(Long userId, String state) {
        log.info("getAllBookingsByUserId by state - {}", state);
        userService.findById(userId).orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        switch (state) {
            case "ALL" -> {
                return repository.findAllBookingsByBookerIdOrderByStartDesc(userId);
            }
            case "CURRENT" -> {
                return repository.findAllBookingByCurrentDate(userId, LocalDateTime.now());
            }
            case "PAST" -> {
                return repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                return repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "WAITING" -> {
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING);
            }
            case "REJECTED" -> {
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED);
            }
            default -> throw new ValidationException("status - " + state + " is not supported");
        }
    }

    @Override
    public Collection<Booking> getBookingsForAllItemsByUserId(Long userId, String state) {
        log.info("getBookingsAllItemsByUserId by state - {}", state);
        userService.findById(userId).orElseThrow(() -> new NotFoundException("User by id: " + userId + " not found"));
        switch (state) {
            case "ALL" -> {
                return repository.findAllByItemOwnerIdOrderByStartDesc(userId);
            }
            case "CURRENT" -> {
                return repository.findAllByBookingItemsCurrentDate(userId, LocalDateTime.now());
            }
            case "PAST" -> {
                return repository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                return repository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "WAITING" -> {
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING);
            }
            case "REJECTED" -> {
                return repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED);
            }
            default -> throw new ValidationException("status - " + state + " is not supported");
        }
    }

    private void changeStatusApprovedOrRejected(Booking booking, Boolean state) {
        if (state) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }
    }

    @Override
    public Booking findLastBooking(Item item) {
        return repository
                .findFirstByItemIdAndEndBeforeOrderByStartDesc(item.getId(), LocalDateTime.now().minusSeconds(5))
                .orElse(null);
    }

    @Override
    public Booking findNextBooking(Item item) {
        return repository
                .findFirstByItemIdAndStartAfterOrderByStart(item.getId(), LocalDateTime.now())
                .orElse(null);
    }
}
