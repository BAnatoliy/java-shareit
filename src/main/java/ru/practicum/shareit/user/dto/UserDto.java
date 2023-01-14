package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.item.dto.ItemSlimDto;
import ru.practicum.shareit.user.valid.UserValidGroups;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(groups = UserValidGroups.OnCreate.class)
    @Email(groups = {UserValidGroups.OnCreate.class, UserValidGroups.OnUpdate.class})
    private String email;
    @Size(min = 4, groups = UserValidGroups.OnCreate.class)
    @NotBlank(groups = UserValidGroups.OnCreate.class)
    @NotNull(groups = UserValidGroups.OnCreate.class)
    private String name;
    private Set<ItemSlimDto> items = new HashSet<>();
    private Set<BookingSlimDto> bookings = new HashSet<>();
}
