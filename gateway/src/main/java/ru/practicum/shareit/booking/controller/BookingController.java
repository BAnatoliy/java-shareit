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
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		//BookingState state = BookingState.from(stateParam)
				//.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookingRequestDto bookingRequestDto) {
		log.info("Creating booking {}, userId={}", bookingRequestDto, userId);
		return bookingClient.bookItem(userId, bookingRequestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByOwner(
			@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestHeader(value = "X-Sharer-User-Id") long userId) {
		//BookingState state = BookingState.from(stateParam)
				//.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getBookingsByOwner(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> confirmBooking(
			@PathVariable Long bookingId,
			@RequestParam(value = "approved") Boolean approved,
			@RequestHeader(value = "X-Sharer-User-Id") long userId) {
		return bookingClient.confirmBooking(userId, bookingId, approved);
	}

	/*@PostMapping
	public BookingDto createBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto,
									@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
		return bookingService.createBooking(bookingRequestDto, userId);
	}*/

	/*@PatchMapping("/{bookingId}")
	public BookingDto confirmBooking(@PathVariable Long bookingId, @RequestParam(value = "approved") Boolean approved,
									 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
		return bookingService.confirmBooking(bookingId, approved, userId);
	}*/

	/*@GetMapping("/{bookingId}")
	public BookingDto getBookingById(@PathVariable Long bookingId,
									 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
		return bookingService.getBookingById(bookingId, userId);
	}*/

	/*@GetMapping
	public List<BookingDto> getBookingsByBooker(@RequestParam(value = "state", defaultValue = "ALL") State state,
												@RequestParam(value = "from", required = false) Integer from,
												@RequestParam(value = "size", required = false) Integer size,
												@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
		return bookingService.getBookingByBooker(state, from, size, userId);
	}*/

	/*@GetMapping("/owner")
	public List<BookingDto> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") State state,
											   @RequestParam(value = "from", required = false) Integer from,
											   @RequestParam(value = "size", required = false) Integer size,
											   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
		return bookingService.getBookingByOwner(state, from, size, userId);
	}*/
}
