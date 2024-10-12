package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository requestRepository;
    UserService userService;
    ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto);
        itemRequest.setRequestor(userService.findUserById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestAnswerDto> getAllItemRequestByUserId(Long userId) {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toAnswerDto(
                        itemRequest,
                        getItemsByItemRequestId(itemRequest.getId()))
                )
                .toList();
    }

    @Override
    public List<ItemRequestAnswerDto> getAllItemRequestButThisUserId(Long userId) {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toAnswerDto(
                        itemRequest,
                        getItemsByItemRequestId(itemRequest.getId()))
                )
                .toList();
    }

    @Override
    public ItemRequestAnswerDto getById(Long itemRequestId) {
        ItemRequest itemRequest = requestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Item by id: " + itemRequestId + " not found"));
        List<Item> items = getItemsByItemRequestId(itemRequestId);
        System.out.println(items);
        return ItemRequestMapper.toAnswerDto(itemRequest, items);
    }

    private  List<Item> getItemsByItemRequestId(Long itemRequestId) {
        return itemRepository.findAllByRequestId(itemRequestId);
    }
}
