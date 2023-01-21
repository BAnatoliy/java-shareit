package ru.practicum.shareit.item.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID = {} is found", userId);
                    return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                            userId));
                }
        ));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElse(null));
        }
        Item savedItem = itemRepository.save(item);
        log.debug("Item created");
        return itemMapper.mapToDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemMapper.mapToItem(itemDto);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.debug("Item with ID = {} is found", itemId);
                    return new EntityNotFoundException(String
                            .format("Item with ID = %s not found. ID is wrong", itemId));
                });

        if (!Objects.equals(oldItem.getOwner().getId(), userId)) {
            throw new EntityNotFoundException("This item belongs to other owner");
        }

        String itemName = item.getName();
        String itemDescription = item.getDescription();
        if (itemName != null) {
            if (itemName.isBlank()) {
                throw new ValidationException("Name cannot be empty");
            }
            oldItem.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        if (itemDescription != null) {
            if (itemDescription.isBlank()) {
                throw new ValidationException("Description cannot be empty");
            }
            oldItem.setDescription(item.getDescription());
        }
        Item saveItem = itemRepository.save(oldItem);
        log.debug("Item updated");
        return itemMapper.mapToDto(saveItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Item with ID = {} is found", itemId);
            return new EntityNotFoundException(String.format("Item with ID = %s not found. ID is wrong",
                    itemId));
        });

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllByItem_Id(itemId);
            List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
            getItemList(bookings, item, comments);
        }
        log.debug("Item with ID = {} is found", itemId);
        return itemMapper.mapToDto(item);
    }

    @Override
    public List<ItemDto> getItemsByTheOwner(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        List<Item> itemList = itemRepository.findByOwnerIdOrderById(userId);
        List<Booking> bookings =
                bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(userId, true);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemList.stream().map(Item::getId).collect(Collectors.toList()));

        for (Item item : itemList) {
            getItemList(bookings, item, comments);
        }
        log.debug("Get item`s list with owner`s ID = {}", userId);
        return itemList.stream().map(itemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.findAvailableItemByNameAndDescription(text);
        log.debug("Get item`s list contain {}", text);
        return itemList.stream().map(itemMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentRequestDto commentRequestDto, Long itemId, Long userId) {
        Comment comment = itemMapper.mapToComment(commentRequestDto);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Item with ID = {} is found", itemId);
            return new EntityNotFoundException(String.format("Item with ID = %s not found. ID is wrong",
                    itemId));
        });
        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.debug("Item with ID = {} is found", itemId);
            return new EntityNotFoundException(String.format("Item with ID = %s not found. ID is wrong",
                    itemId));
        });

        long count = item.getBookings().stream().filter(booking -> booking.getBooker().getId().equals(userId) &&
                booking.getEnd().isBefore(LocalDateTime.now())).count();
        if (count < 1) {
            throw new ItemCheckException("User has not book this item yet");
        }

        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment for Item with ID {} saved", itemId);
        return itemMapper.mapToCommentDto(savedComment);
    }

    private void getItemList(List<Booking> bookings, Item item, List<Comment> comments) {
        List<Booking> itemBookings = bookings.stream()
                .filter(booking -> booking.getItem().equals(item))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());

        Iterator<Booking> iterator = itemBookings.iterator();
        Booking lastBooking = null;
        Booking nextBooking = null;

        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                if (item.getLastBooking() == null) {
                    lastBooking = booking;
                }
                if (item.getLastBooking() != null && item.getLastBooking().getEnd().isBefore(booking.getEnd())) {
                    lastBooking = booking;
                }
            }

            if (booking.getStart().isAfter(LocalDateTime.now())) {
                if (item.getNextBooking() == null) {
                    nextBooking = booking;
                }
                if (item.getNextBooking() != null &&
                        item.getNextBooking().getStart().isBefore(booking.getStart())) {
                    nextBooking = booking;
                }
            }
        }

        Set<Comment> itemComments = comments.stream()
                .filter(i -> i.getItem().getId().equals(item.getId()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Comment::getCreated))));

        item.setComments(itemComments);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
    }
}
