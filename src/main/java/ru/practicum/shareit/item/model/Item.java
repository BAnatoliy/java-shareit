package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Item {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @NotNull
    private User owner;
}
