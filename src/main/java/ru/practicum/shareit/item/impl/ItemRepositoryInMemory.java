package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class ItemRepositoryInMemory {
    private final Map<Long, Item> items = new HashMap<>();
    private long itemId = 1;

    //@Override
    public Item createItem(Item item) {
        item.setId(itemId);
        if (items.containsValue(item)) {
            throw new ValidationException("This item has already created");
        }
        items.put(item.getId(), item);
        generatedId();
        return item;
    }

    //@Override
    public Item updateItem(Item item, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Item not found");
        }
        Item itemForUpdate = items.get(itemId);
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        Boolean available = item.getAvailable();
        User itemOwner = item.getUser();
        itemForUpdate.setUser(itemOwner);
        if (itemName != null) {
            itemForUpdate.setName(itemName);
        }
        if (itemDescription != null) {
            itemForUpdate.setDescription(itemDescription);
        }
        if (available != null) {
            itemForUpdate.setAvailable(available);
        }
        items.put(itemId, itemForUpdate);
        return itemForUpdate;
    }

    //@Override
    public Item getItemById(Long itemId) {
        if (itemId == null || !items.containsKey(itemId)) {
            throw new EntityNotFoundException("Item not found");
        }
        return items.get(itemId);
    }

    //@Override
    public List<Item> getAvailableItem(String text) {
        List<Item> availableItems = new ArrayList<>();
        if (!text.isBlank()) {
            items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .forEach(availableItems::add);
        }
        return availableItems;
    }

    private void generatedId() {
        itemId++;
    }
}
