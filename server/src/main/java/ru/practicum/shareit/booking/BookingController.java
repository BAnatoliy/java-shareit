package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

//import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(@PathVariable Long bookingId, @RequestParam(value = "approved") Boolean approved,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.confirmBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestParam(value = "state", defaultValue = "ALL") State state,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByBooker(state, from, size, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") State state,
                                               @RequestParam(value = "from", required = false) Integer from,
                                               @RequestParam(value = "size", required = false) Integer size,
                                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingByOwner(state, from, size, userId);
    }
}
