package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@UtilityClass
public class ItemMapper {

    public static ItemDto toDto(Item item, BookingService bookingService, ItemService itemService, Long userId) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .lastBooking(bookingService.findLastBooking(userId))
                .nextBooking(bookingService.findNextBooking(userId))
                .comments(itemService.getComments(item))
                .build();
    }

    public static Collection<ItemDto> toDto(Collection<Item> items, BookingService bookingService,
                                            ItemService itemService, Long userId) {
        return items.stream()
                .map(item -> ItemMapper.toDto(item, bookingService, itemService, userId))
                .toList();
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public static ItemShortDto toShortDto(Item item, BookingService bookingService,
                                          ItemService itemService, Long userId) {
        if (item == null) {
            return null;
        }
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(bookingService.findLastBooking(userId))
                .nextBooking(bookingService.findNextBooking(userId))
                .comments(itemService.getComments(item))
                .build();
    }

    public static Collection<ItemShortDto> toShortDto(Collection<Item> items, BookingService bookingService,
                                                      ItemService itemService, Long userId) {
        return items.stream()
                .map(item -> ItemMapper.toShortDto(item, bookingService, itemService, userId))
                .toList();
    }

    public static ItemFromItemRequestDto fromItemRequestDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemFromItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
