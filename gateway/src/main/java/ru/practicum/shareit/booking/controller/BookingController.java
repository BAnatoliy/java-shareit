package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookingRequestDto bookingRequestDto) {
		log.info("Creating booking {}, userId={}", bookingRequestDto, userId);
		return bookingClient.bookItem(userId, bookingRequestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
											 @Positive @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(
			@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@Positive @RequestHeader(value = "X-Sharer-User-Id") long userId) {
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> confirmBooking(
			@Positive @PathVariable Long bookingId,
			@RequestParam(value = "approved") Boolean approved,
			@Positive @RequestHeader(value = "X-Sharer-User-Id") long userId) {
		return bookingClient.confirmBooking(userId, bookingId, approved);
	}
}
