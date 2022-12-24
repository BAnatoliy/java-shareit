package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item, Long itemId);

    Item getItemById(Long itemId);

    List<Item> getAvailableItem(String text);

}
