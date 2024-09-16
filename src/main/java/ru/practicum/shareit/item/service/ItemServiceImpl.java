package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryMemory;

import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryMemory itemRepository;

    public ItemServiceImpl(ItemRepositoryMemory itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto getItemsById(Long itemId) {
        return itemRepository.getItemsById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item by id: " + itemId + " not found"));
    }

    @Override
    public Collection<ItemShortDto> getItemsByUserId(Long userId) {
        return ItemMapper.toShortDto(itemRepository.getItemsByUserId(userId));
    }

    @Override
    public Collection<ItemDto> getItemsByText(String text) {
        return itemRepository.getItemsByText(text.toLowerCase()).stream()
                .map(ItemMapper::toDto).toList();
    }

    @Override
    public ItemDto create(Long userId, ItemDto item) {
        Item newItem = ItemMapper.toItem(item);
        return ItemMapper.toDto(itemRepository.create(userId, newItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        Item updItem = ItemMapper.toItem(item);
        return ItemMapper.toDto(itemRepository.update(userId, itemId, updItem));
    }
}
