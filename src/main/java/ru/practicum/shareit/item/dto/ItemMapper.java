package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@UtilityClass
public class ItemMapper {

    public static ItemDto toDto(Item item, BookingService bookingService, ItemService itemService) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .lastBooking(bookingService.findLastBooking(item))
                .nextBooking(bookingService.findNextBooking(item))
                .comments(itemService.getComments(item))
                .build();
    }

    public static Collection<ItemDto> toDto(Collection<Item> items, BookingService bookingService, ItemService itemService) {
        return items.stream()
                .map(item -> ItemMapper.toDto(item, bookingService, itemService))
                .toList();
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public static ItemShortDto toShortDto(Item item, BookingService bookingService, ItemService itemService) {
        if (item == null) {
            return null;
        }
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(bookingService.findLastBooking(item))
                .nextBooking(bookingService.findNextBooking(item))
                .comments(itemService.getComments(item))
                .build();
    }

    public static Collection<ItemShortDto> toShortDto(Collection<Item> items, BookingService bookingService, ItemService itemService) {
        return items.stream()
                .map(item -> ItemMapper.toShortDto(item, bookingService, itemService))
                .toList();
    }
}
