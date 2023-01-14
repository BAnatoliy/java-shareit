package ru.practicum.shareit.booking.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingApprovedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;


    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    @Override
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        Booking booking = bookingMapper.mapToBooking(bookingRequestDto);
        Long itemId = bookingRequestDto.getItemId();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Item with ID = {} is found", itemId);
            return new EntityNotFoundException(String.format("Item with ID = %s not found. ID is wrong",
                    itemId));
        });
        if (!item.getAvailable()) {
            throw new ItemCheckException("Item is unavailable");
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Owner cannot book him item");

        }

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking bookingResponse = bookingRepository.save(booking);
        log.debug("Booking created");
        return bookingMapper.mapToDto(bookingResponse);
    }

    @Transactional
    @Override
    public BookingDto confirmBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Booking with ID = {} is found", bookingId);
            return new EntityNotFoundException(String.format("Booking with ID = %s not found. ID is wrong",
                    bookingId));
        });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("User is not the owner of the item");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingApprovedException("Booking is approved");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Booking status is updated on approved");
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Booking status is updated on rejected");
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.debug("Booking update after approve or rejected");
        return bookingMapper.mapToDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.debug("Booking with ID = {} is found", bookingId);
            return new EntityNotFoundException(String.format("Booking with ID = %s not found. ID is wrong",
                    bookingId));
        });
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return bookingMapper.mapToDto(booking);
        } else {
            throw new EntityNotFoundException("User is not the owner of the item or booking has other booker");
        }
    }

    @Override
    public List<BookingDto> getBookingByBooker(State state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId).stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, now).stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, now).stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(userId, now, now)
                        .stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING)
                        .stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream().map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Override
    public List<BookingDto> getBookingByOwner(State state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        List<Booking> bookingList =
                bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(userId, true);

        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return bookingList.stream()
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingList.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingList.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingList.stream()
                        .filter(booking -> booking.getEnd().isAfter(now) && booking.getStart().isBefore(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingList.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(bookingMapper::mapToDto)
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }
}
