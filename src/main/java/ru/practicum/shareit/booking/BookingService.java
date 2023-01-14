package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto confirmBooking(Long bookingId, Boolean approved, Long userid);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingByBooker(State state, Long userId);

    List<BookingDto> getBookingByOwner(State state, Long userId);
}
