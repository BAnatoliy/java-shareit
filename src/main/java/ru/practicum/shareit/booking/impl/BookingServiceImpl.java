package ru.practicum.shareit.booking.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking createBooking(Booking booking, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Item with ID = {} is found", itemId);
            return new EntityNotFoundException(String.format("Item with ID = %s not found. ID is wrong",
                    itemId));
        });
        if (!item.getAvailable()) {
            throw new ItemUnavailableException();
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        log.debug("Booking created");
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking confirmBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Booking with ID = {} is found", bookingId);
            return new EntityNotFoundException(String.format("Booking with ID = %s not found. ID is wrong",
                    bookingId));
        });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("User is not the owner of the item");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Booking status is updated on approved");
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Booking status is updated on rejected");
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Booking with ID = {} is found", bookingId);
            return new EntityNotFoundException(String.format("Booking with ID = %s not found. ID is wrong",
                    bookingId));
        });
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return booking;
        } else {
            throw new ValidationException("User is not the owner of the item or booking has other booker");
        }
    }

    @Override
    public List<Booking> getBookingByBooker(State state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        switch (state) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndEndAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<Booking> getBookingByOwner(State state, Long userId) {
        return null;
    }
}
