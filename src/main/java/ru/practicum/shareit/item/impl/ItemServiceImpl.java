package ru.practicum.shareit.item.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

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

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
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
        itemRepository.save(item);
        log.debug("Item created");
        return item;
    }

    @Transactional
    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(
                () -> {
                    log.debug("Item with ID = {} is found", itemId);
                    return new EntityNotFoundException(String.
                            format("Item with ID = %s not found. ID is wrong", itemId));
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
        itemRepository.save(oldItem);
        log.debug("Item updated");
        return oldItem;
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
            getItemList(bookings, item);
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

        for (Item item : itemList) {
            getItemList(bookings, item);
            /*List<Booking> itemBookings = bookings.stream()
                    .filter(booking -> booking.getItem().equals(item))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .collect(Collectors.toList());

            Iterator<Booking> iterator = itemBookings.iterator();
            Booking lastBooking = null;
            Booking nextBooking = null;

            while (iterator.hasNext()) {
                Booking booking = iterator.next();
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    if(item.getLastBooking() == null) {
                        lastBooking = booking;
                    }
                    if(item.getLastBooking() != null && item.getLastBooking().getEnd().isBefore(booking.getEnd())) {
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
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);*/
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

    private void getItemList(List<Booking> bookings, Item item) {
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
                if(item.getLastBooking() == null) {
                    lastBooking = booking;
                }
                if(item.getLastBooking() != null && item.getLastBooking().getEnd().isBefore(booking.getEnd())) {
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
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
    }

    @Override
    public Comment createComment(Comment comment, Long itemId, Long userId) {
        return null;
    }
}
