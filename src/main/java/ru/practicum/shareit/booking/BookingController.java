package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        Booking booking = bookingMapper.mapToBooking(bookingRequestDto);
        Long itemId = bookingRequestDto.getItemId();
        return bookingMapper.mapToDto(bookingService.createBooking(booking, userId, itemId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(@PathVariable Long bookingId, @RequestParam(value = "approved") Boolean approved,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingMapper.mapToDto(bookingService.confirmBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingMapper.mapToDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestParam(value = "state", defaultValue = "ALL") State state,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByBooker(state, userId).stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") State state,
                                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByOwner(state, userId).stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
