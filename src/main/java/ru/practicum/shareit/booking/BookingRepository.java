package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);
    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);
    List<Booking> findAllByBooker_IdAndEndAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);
    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);


}
