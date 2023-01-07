package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.item.valid.ItemValidGroups;
import ru.practicum.shareit.user.dto.UserSlimDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = ItemValidGroups.OnCreate.class)
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    @Size(max = 50, groups = {ItemValidGroups.OnCreate.class, ItemValidGroups.OnUpdate.class})
    private String name;
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    @Size(max = 200, groups = {ItemValidGroups.OnCreate.class, ItemValidGroups.OnUpdate.class})
    private String description;
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    private Boolean available;
    private UserSlimDto owner;
    private BookingSlimDto lastBooking;
    private BookingSlimDto nextBooking;
    private Set<BookingSlimDto> bookings = new HashSet<>();
}
