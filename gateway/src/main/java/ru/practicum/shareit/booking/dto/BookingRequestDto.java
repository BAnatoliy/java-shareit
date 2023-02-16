package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.valid.BookingValid;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@BookingValid
public class BookingRequestDto {
    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}

