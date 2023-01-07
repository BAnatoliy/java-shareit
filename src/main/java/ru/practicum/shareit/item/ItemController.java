package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.valid.ItemValidGroups;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    @Validated(ItemValidGroups.OnCreate.class)
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.mapToItem(itemDto);
        return itemMapper.mapToDto(itemService.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(ItemValidGroups.OnUpdate.class)
    public ItemDto updateItem(@RequestBody @Valid ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.mapToItem(itemDto);
        return itemMapper.mapToDto(itemService.updateItem(item, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemMapper.mapToDto(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public List<ItemDto> getItemsByTheOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByTheOwner(userId).stream().map(itemMapper::mapToDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItem(@RequestParam(value = "text") String text,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.getAvailableItem(text).stream().map(itemMapper::mapToDto).collect(Collectors.toList());
    }
}
