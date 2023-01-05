package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
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
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    private User user;
}
