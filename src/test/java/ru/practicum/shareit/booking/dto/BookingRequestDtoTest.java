package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.constant.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestDtoTest {

    @Test
    void setId() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setId(1L);
        assertEquals(1L, bookingRequestDto.getId());
    }

    @Test
    void setItemId() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        assertEquals(1L, bookingRequestDto.getItemId());
    }

    @Test
    void setStart() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.of(2023, 1, 27, 15, 40));
        assertEquals(LocalDateTime.of(2023, 1, 27, 15, 40),
                bookingRequestDto.getStart());
    }

    @Test
    void setEnd() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setEnd(LocalDateTime.of(2023, 1, 27, 15, 40));
        assertEquals(LocalDateTime.of(2023, 1, 27, 15, 40),
                bookingRequestDto.getEnd());
    }

    @Test
    void setStatus() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, 2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        assertEquals(BookingStatus.WAITING, bookingRequestDto.getStatus());
    }
}