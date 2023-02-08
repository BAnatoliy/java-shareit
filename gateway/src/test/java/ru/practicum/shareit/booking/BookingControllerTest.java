package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exception.ErrorHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @SneakyThrows
    @Test
    void createBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.WAITING);
        ResponseEntity<Object> response = ResponseEntity.ok(bookingRequestDto);

        when(bookingClient.bookItem(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(response);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void createBookingTest_whenStartIsBeforeNow_shouldStatusCodeIs400() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.WAITING);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createBookingTest_whenStartIsAfterEnd_shouldStatusCodeIs400() {
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.WAITING);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void confirmBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.APPROVED);
        ResponseEntity<Object> response = ResponseEntity.ok(bookingRequestDto);
        when(bookingClient.confirmBooking(1L, 1L, true))
                .thenReturn(response);

        MockHttpServletRequestBuilder request = patch("/bookings/1")
                .param("approved", "true")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.APPROVED);
        ResponseEntity<Object> response = ResponseEntity.ok(bookingRequestDto);
        when(bookingClient.getBooking(1L, 1L))
                .thenReturn(response);

        MockHttpServletRequestBuilder request = get("/bookings/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getBookingsByBooker() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, 2L, start, end, BookingStatus.WAITING);
        BookingRequestDto bookingRequestDto2 = new BookingRequestDto(
                2L, 1L, start, end, BookingStatus.WAITING);
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(bookingRequestDto, bookingRequestDto2));
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(response);

        MockHttpServletRequestBuilder request = get("/bookings")
                .header("X-Sharer-User-Id", "1")
                .param("from", "1")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));
    }
}