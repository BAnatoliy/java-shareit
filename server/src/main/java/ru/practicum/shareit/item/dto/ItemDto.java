package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.user.dto.UserSlimDto;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserSlimDto owner;
    private Long requestId;
    private BookingSlimDto lastBooking;
    private BookingSlimDto nextBooking;
    private Set<BookingSlimDto> bookings = new HashSet<>();
    private Set<CommentDto> comments = new HashSet<>();
}
