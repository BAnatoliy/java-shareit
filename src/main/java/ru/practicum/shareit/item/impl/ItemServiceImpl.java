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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    @Override
    public Item createItem(Item item, Long userId) {
        item.setOwner(userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID = {} is found", userId);
                    return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                            userId));
                }
        ));
        Item savedItem = itemRepository.save(item);
        log.debug("Item created");
        return savedItem;
    }

    @Transactional
    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
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
        return saveItem;
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
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
        return item;
    }

    @Override
    public List<Item> getItemsByTheOwner(Long userId) {
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
        return itemList;
    }

    @Override
    public List<Item> getAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemRepository.findAvailableItemByNameAndDescription(text);
        log.debug("Get item`s list contain {}", text);
        return itemList;
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

    @Override
    public Comment createComment(Comment comment, Long itemId, Long userId) {
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
        return savedComment;
    }
}
