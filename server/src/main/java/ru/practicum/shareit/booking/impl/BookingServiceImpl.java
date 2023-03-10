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
import ru.practicum.shareit.pageableImpl.CustomPageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final String logForParameters = "Get list of bookings parameters: state = {}, size = {}, from = {}";


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
    public List<BookingDto> getBookingByBooker(State state, Integer from, Integer size, Long userId) {
        checkParametersFromAndSize(from, size);
        userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong",
                    userId));
        });

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                } else {
                    bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId,
                            CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case PAST:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
                } else {
                    bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId,
                            now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case FUTURE:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
                } else {
                    bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId,
                            now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case CURRENT:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(userId,
                            now, now);
                } else {
                    bookings = bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                                    userId, now, now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case WAITING:
                return getBookingDtosByStatus(BookingStatus.WAITING, state, from, size, userId);
            default:
                return getBookingDtosByStatus(BookingStatus.REJECTED, state, from, size, userId);
        }
    }

    @Override
    public List<BookingDto> getBookingByOwner(State state, Integer from, Integer size, Long userId) {
        checkParametersFromAndSize(from, size);
        userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User with ID = {} is found", userId);
            return new EntityNotFoundException(String.format("User with ID = %s not found. ID is wrong", userId));
        });

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                            userId, true);
                } else {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                            userId, true, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case PAST:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
                            userId, true, now);
                } else {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
                            userId, true, now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case FUTURE:
                if (from == null || size == null) {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
                            userId, true, now);
                } else {
                    bookings = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
                            userId, true, now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case CURRENT:
                if (from == null || size == null) {
                    bookings = bookingRepository
                            .findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                            userId, true, now, now);
                } else {
                    bookings = bookingRepository
                            .findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                            userId, true, now, now, CustomPageRequest.of(from, size));
                }
                log.debug(logForParameters, state, size, from);
                return bookingMapper.mapToListDto(bookings);
            case WAITING:
                return getBookingDtosByOwnerAndByStatus(BookingStatus.WAITING, state, from, size, userId);
            default:
                return getBookingDtosByOwnerAndByStatus(BookingStatus.REJECTED, state, from, size, userId);
        }
    }

    private void checkParametersFromAndSize(Integer from, Integer size) {
        if ((from != null && from < 0) || (size != null && size <= 0)) {
            log.debug("Parameters cannot be negative");
            throw new ItemCheckException("Parameters cannot be negative");
        }
    }

    private List<BookingDto> getBookingDtosByStatus(BookingStatus status, State state, Integer from,
                                                    Integer size, Long userId) {
        List<Booking> bookings;
        if (from == null || size == null) {
            bookings = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId,
                    status);
        } else {
            bookings = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId,
                    status, CustomPageRequest.of(from, size));
        }
        log.debug(logForParameters, state, size, from);
        return bookingMapper.mapToListDto(bookings);
    }

    private List<BookingDto> getBookingDtosByOwnerAndByStatus(BookingStatus status, State state, Integer from,
                                                    Integer size, Long userId) {
        List<Booking> bookings;
        if (from == null || size == null) {
            bookings = bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(userId,
                    status);
        } else {
            bookings = bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(userId,
                    status, CustomPageRequest.of(from, size));
        }
        log.debug(logForParameters, state, size, from);
        return bookingMapper.mapToListDto(bookings);
    }
}
