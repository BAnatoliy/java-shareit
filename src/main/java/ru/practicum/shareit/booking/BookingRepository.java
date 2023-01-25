package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId,
                                                                 LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
            Long userId, LocalDateTime dateTime, LocalDateTime dateTime2);

    List<Booking> findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
            Long userId, LocalDateTime dateTime, LocalDateTime dateTime2, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId,
                                                                  LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus bookingStatus,
                                                                Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(Long userId, Boolean available);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(Long userId,
                                                                          Boolean available, Pageable pageable);

    List<Booking> findAllByItem_Id(Long itemId);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime, LocalDateTime dateTime2);

    List<Booking> findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
            Long userId, Boolean available, LocalDateTime dateTime, LocalDateTime dateTime2, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(Long userId,
                                                                    BookingStatus bookingStatus, Pageable pageable);
}
