package ru.practicum.shareit.itemRequest.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemSlimDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "name", "e@ya.ru", null, null);
        itemRequest = new ItemRequest(1L, "desc",
                LocalDateTime.of(2023, 1, 27, 15, 45), null, user);
        itemRequestDto = new ItemRequestDto(1L, "desc",
                LocalDateTime.of(2023, 1, 27, 15, 45), null);
        item = new Item(1L, "name", "desc", true, user, null, null,
                null, null, itemRequest);
    }

    @Test
    void mapToDtoTest() {
        ItemRequestDto itemRequestDtoReturn = itemRequestMapper.mapToDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDtoReturn.getId());
    }

    @Test
    void mapToItemRequestTest() {
        ItemRequest itemRequestReturn = itemRequestMapper.mapToItemRequest(itemRequestDto);
        assertEquals(itemRequestDto.getId(), itemRequestReturn.getId());
    }

    @Test
    void mapToItemDtoForRequestTest() {
        ItemSlimDtoForRequest itemSlimDtoReturn = itemRequestMapper.mapToItemDtoForRequest(item);
        assertEquals(item.getId(), itemSlimDtoReturn.getId());
    }

    @Test
    void idFromRequestTest() {
        Long id = itemRequestMapper.idFromRequest(itemRequest);
        assertEquals(itemRequest.getId(), id);
    }
}
