package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.valid.ItemValidGroups;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated(ItemValidGroups.OnCreate.class)
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(ItemValidGroups.OnUpdate.class)
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByTheOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "from", required = false) Integer from,
                                            @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getItemsByTheOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getAvailableItem(@RequestParam(value = "text") String text,
                                          @RequestParam(value = "from", required = false) Integer from,
                                          @RequestParam(value = "size", required = false) Integer size,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.getAvailableItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentRequestDto commentRequestDto, @PathVariable Long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.createComment(commentRequestDto, itemId, userId);
    }
}
