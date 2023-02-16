package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserSlimDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ItemController itemController;
    @Autowired
    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @SneakyThrows
    @Test
    void createItemTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        UserSlimDto owner = new UserSlimDto(1L, "user", "us@ya.ru");
        BookingSlimDto booking = new BookingSlimDto(1L, start, end, BookingStatus.WAITING, 2L);
        BookingSlimDto booking2 = new BookingSlimDto(2L, start, end, BookingStatus.WAITING, 2L);
        ItemDto item = new ItemDto(1L, "name", "desc", true, owner, 1L, booking,
                booking2, new HashSet<>(), new HashSet<>());
        when(itemService.createItem(any(ItemDto.class), anyLong())).thenReturn(item);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        UserSlimDto owner = new UserSlimDto(1L, "user", "us@ya.ru");
        BookingSlimDto booking = new BookingSlimDto(1L, start, end, BookingStatus.WAITING, 2L);
        BookingSlimDto booking2 = new BookingSlimDto(2L, start, end, BookingStatus.WAITING, 2L);
        ItemDto item = new ItemDto(1L, "name", "desc", true, owner, 1L, booking,
                booking2, new HashSet<>(), new HashSet<>());
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(item);

        MockHttpServletRequestBuilder request = patch("/items/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        UserSlimDto owner = new UserSlimDto(1L, "user", "us@ya.ru");
        BookingSlimDto booking = new BookingSlimDto(1L, start, end, BookingStatus.WAITING, 2L);
        BookingSlimDto booking2 = new BookingSlimDto(2L, start, end, BookingStatus.WAITING, 2L);
        ItemDto item = new ItemDto(1L, "name", "desc", true, owner, 1L, booking,
                booking2, new HashSet<>(), new HashSet<>());
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        MockHttpServletRequestBuilder request = get("/items/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getItemsByTheOwnerTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        UserSlimDto owner = new UserSlimDto(1L, "user", "us@ya.ru");
        BookingSlimDto booking = new BookingSlimDto(1L, start, end, BookingStatus.WAITING, 2L);
        BookingSlimDto booking2 = new BookingSlimDto(2L, start, end, BookingStatus.WAITING, 2L);
        ItemDto item = new ItemDto(1L, "name", "desc", true, owner, 1L, booking,
                booking2, new HashSet<>(), new HashSet<>());
        ItemDto item2 = new ItemDto(2L, "name2", "desc2", true, owner, 1L, booking,
                booking2, new HashSet<>(), new HashSet<>());
        List<ItemDto> items = List.of(item, item2);
        when(itemService.getItemsByTheOwner(anyLong(), anyInt(), anyInt())).thenReturn(items);

        MockHttpServletRequestBuilder request = get("/items")
                .header("X-Sharer-User-Id", "1")
                .param("from", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(null, "text");
        CommentDto commentDto = new CommentDto(1L, "text", "user", LocalDateTime.now());
        when(itemService.createComment(any(CommentRequestDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        MockHttpServletRequestBuilder request = post("/items/1/comment")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }
}