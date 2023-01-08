package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking, Long userid, Long itemId);

    Booking confirmBooking(Long bookingId, Boolean approved, Long userid);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getBookingByBooker(State state, Long userId);

    List<Booking> getBookingByOwner(State state, Long userId);
}
