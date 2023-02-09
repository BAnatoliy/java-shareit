package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                         @Positive @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersRequests(@Positive @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.getUsersRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @Positive @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@Positive @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                     @Positive @PathVariable Long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}

