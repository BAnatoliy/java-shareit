package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId, Long userId);

    List<Item> getItemsByTheOwner(Long userId);

    List<Item> getAvailableItem(String text);

    Comment createComment(Comment comment, Long itemId, Long userId);
}
