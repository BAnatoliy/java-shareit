package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.item.dto.ItemSlimDto;
import ru.practicum.shareit.user.valid.UserValidGroups;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Set<ItemSlimDto> items = new HashSet<>();
    private Set<BookingSlimDto> bookings = new HashSet<>();
}
