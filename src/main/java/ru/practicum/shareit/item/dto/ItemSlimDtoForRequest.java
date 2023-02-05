package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSlimDtoForRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
