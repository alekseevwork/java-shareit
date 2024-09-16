package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.error.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class ItemServiceDb implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    public ItemServiceDb(ItemRepository repository, @Qualifier("userServiceDb") UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public ItemDto getItemsById(Long itemId) {
        return repository.findItemWithOwnerById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item by id: " + itemId + " not found"));
    }

    @Override
    public Collection<ItemShortDto> getItemsByUserId(Long userId) {
        return ItemMapper.toShortDto(repository.findAllByOwnerId(userId));
    }

    @Override
    public Collection<ItemDto> getItemsByText(String text) {
        System.out.println(text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toDto(repository.findAllByNamePattern(text));
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.findUserById(userId));
        itemDto.setOwner(owner);

        Item item = repository.save(ItemMapper.toItem(itemDto));
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        ItemDto oldItem = getItemsById(itemId);
        User owner = UserMapper.toUser(userService.findUserById(userId));
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
        Item item = repository.save(ItemMapper.toItem(oldItem));
        return ItemMapper.toDto(item);
    }
}
