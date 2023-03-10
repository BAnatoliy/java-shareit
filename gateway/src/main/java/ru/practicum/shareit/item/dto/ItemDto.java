package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.valid.ItemValidGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private Long requestId;
}
