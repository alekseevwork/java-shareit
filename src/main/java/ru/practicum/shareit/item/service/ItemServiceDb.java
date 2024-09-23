package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceDb implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceDb(ItemRepository repository,
                         UserService userService,
                         BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.repository = repository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Item getItemsById(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item by id: " + itemId + " not found"));
    }

    @Override
    public Collection<Item> getItemsByUserId(Long userId) {
        return repository.findAllByOwnerId(userId);
    }

    @Override
    public Collection<Item> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.findAllByNamePattern(text);
    }

    @Override
    @Transactional
    public Item create(Long userId, ItemDto itemDto) {
        User owner = userService.findUserById(userId);
        itemDto.setOwner(owner);
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("Name or Description is null.");
        }
        return repository.save(ItemMapper.toItem(itemDto));
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        Item oldItem = getItemsById(itemId);
        User owner = userService.findUserById(userId);
        if (!oldItem.getOwner().equals(owner)) {
            throw new ValidationException("No access User by id - " + owner.getId());
        }
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        return repository.save(oldItem);
    }

    @Override
    @Transactional
    public Comment addComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = CommentMapper.toComment(commentDto);
        Booking booking = bookingRepository
                .findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking not found."));
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getComments(Item item) {
        return CommentMapper.toDto(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()));
    }
}
