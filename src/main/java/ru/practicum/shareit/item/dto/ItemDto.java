package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.valid.ItemValidGroups;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemDto {
    @Min(1)
    @EqualsAndHashCode.Exclude
    private Long Id;
    @NotBlank(groups = ItemValidGroups.OnCreate.class)
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    @Size(max = 50, groups = {ItemValidGroups.OnCreate.class, ItemValidGroups.OnUpdate.class})
    private String name;
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    @Size(max = 200, groups = {ItemValidGroups.OnCreate.class, ItemValidGroups.OnUpdate.class})
    private String description;
    @NotNull(groups = ItemValidGroups.OnCreate.class)
    private Boolean available;
}
