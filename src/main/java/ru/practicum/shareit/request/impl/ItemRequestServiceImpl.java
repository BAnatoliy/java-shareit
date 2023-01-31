package ru.practicum.shareit.request.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRequestMapper itemRequestMapper, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(findUserById(userId));
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.debug("Request created");
        return itemRequestMapper.mapToDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getUsersRequests(Long userId) {
        findUserById(userId);
        List<ItemRequest> listRequest = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId);
        log.debug("Get user`s request list with ID = {}", userId);
        return itemRequestMapper.mapToListDto(listRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        findUserById(userId);
        List<ItemRequestDto> itemRequestList;
        if (from == null || size == null) {
            log.debug("Parameters are null");
            itemRequestList = itemRequestMapper.mapToListDto(
                    itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(userId));
            log.debug("Get all list of request");
            return itemRequestList;
        }
        if (from < 0 || size <= 0) {
            log.debug("Parameters cannot be negative");
            throw new ItemCheckException("Parameters cannot be negative");
        }

        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(userId,
                CustomPageRequest.of(from, size));
        itemRequestList = itemRequestMapper.mapToListDto(itemRequests.getContent());
        log.debug("Get page of requests sorted by create date with {} elements from {}", size, from);
        return itemRequestList;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        findUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> {
                    log.debug("Request with ID = {} is not found", requestId);
                    return new EntityNotFoundException(String.format("Request with ID = %s not found. ID is wrong",
                            requestId));
                });
        log.debug("Request with ID = {} is found", requestId);
        return itemRequestMapper.mapToDto(itemRequest);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID = {} is not found", userId);
                    return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                            userId));
                }
        );
    }
}
