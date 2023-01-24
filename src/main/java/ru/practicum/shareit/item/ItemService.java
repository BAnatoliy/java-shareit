package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByTheOwner(Long userId, Integer from, Integer size);

    List<ItemDto> getAvailableItem(String text, Integer from, Integer size);

    CommentDto createComment(CommentRequestDto commentRequestDto, Long itemId, Long userId);
}
