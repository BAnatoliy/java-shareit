package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
    private BookingService bookingService;
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingDto);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    /*@SneakyThrows
    @Test
    void createBookingTest_whenStartIsBeforeNow_shouldStatusCodeIs400() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingDto);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }*/

    /*@SneakyThrows
    @Test
    void createBookingTest_whenStartIsAfterEnd_shouldStatusCodeIs400() {
        LocalDateTime start = LocalDateTime.now().plusHours(11);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(1L);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingDto);

        MockHttpServletRequestBuilder request = post("/bookings")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }*/

    @SneakyThrows
    @Test
    void confirmBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.confirmBooking(1L, true, 1L))
                .thenReturn(bookingDto);

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
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingDto);

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
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setStart(start);
        bookingDto2.setEnd(end);
        when(bookingService.getBookingByBooker(any(State.class), anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(bookingDto, bookingDto2));

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