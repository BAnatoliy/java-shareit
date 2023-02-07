package ru.practicum.shareit.itemRequest.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemSlimDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestDtoTest {

    @Test
    void setId() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc",
                LocalDateTime.now(), new HashSet<>());
        itemRequestDto.setId(2L);
        assertEquals(2L, itemRequestDto.getId());
    }

    @Test
    void setDescription() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("desc");
        assertEquals("desc", itemRequestDto.getDescription());
    }

    @Test
    void setCreated() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setCreated(LocalDateTime.of(2023, 1, 27, 15, 40));
        assertEquals(LocalDateTime.of(2023, 1, 27, 15, 40),
                itemRequestDto.getCreated());
    }

    @Test
    void setItems() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        HashSet<ItemSlimDtoForRequest> items = new HashSet<>();
        itemRequestDto.setItems(items);
        assertEquals(items, itemRequestDto.getItems());
    }
}