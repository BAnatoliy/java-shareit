package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.valid.ItemValidGroups;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated(ItemValidGroups.OnCreate.class)
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        //return itemService.createItem(itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated(ItemValidGroups.OnUpdate.class)
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemDto itemDto,
                                             @PathVariable Long itemId,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        //return itemService.updateItem(itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByTheOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                            @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        //return itemService.getItemsByTheOwner(userId, from, size);
        return itemClient.getItemsByTheOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItem(@RequestParam(value = "text") String text,
                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        //return itemService.getAvailableItem(text, from, size);
        return itemClient.getAvailableItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentRequestDto commentRequestDto,
                                                @PathVariable Long itemId,
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        //return itemService.createComment(commentRequestDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentRequestDto);
    }
}
