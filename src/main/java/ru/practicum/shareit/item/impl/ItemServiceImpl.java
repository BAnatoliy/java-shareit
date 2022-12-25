package ru.practicum.shareit.item.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        item.setOwner(userRepository.getUserById(userId));
        Item itemToResponse = itemRepository.createItem(item);
        userRepository.addItemByOwner(userId, itemToResponse.getId());
        return itemToResponse;
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        if (!validItemsOwner(itemId, userId)) {
            throw new EntityNotFoundException("This item belongs to other owner");
        }
        item.setOwner(userRepository.getUserById(userId));
        return itemRepository.updateItem(item, itemId);
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getItemsByTheOwner(Long userId) {
        return userRepository.getItemsIdByTheOwner(userId).stream()
                .map(itemRepository::getItemById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableItem(String text) {
        return itemRepository.getAvailableItem(text);
    }

    private boolean validItemsOwner(Long itemId, Long userId) {
        return userRepository.getItemsIdByTheOwner(userId).stream().anyMatch(id -> Objects.equals(id, itemId));
    }
}
