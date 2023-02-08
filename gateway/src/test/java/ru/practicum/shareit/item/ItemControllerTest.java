package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
    private ItemClient itemClient;
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
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, 1L);
        ResponseEntity<Object> response = ResponseEntity.ok(itemDto);
        when(itemClient.createItem(anyLong(), any(ItemDto.class))).thenReturn(response);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void createItemTest_whenItemNameIsEmpty_shouldStatus400() {
        ItemDto itemDto = new ItemDto(1L, "   ", "desc", true, 1L);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createItemTest_whenItemNameIsNull_shouldStatus400() {
        ItemDto itemDto = new ItemDto(1L, null, "desc", true, 1L);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createItemTest_whenDescriptionIsNull_shouldStatus400() {
        ItemDto itemDto = new ItemDto(1L, "name", null, true, 1L);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createItemTest_whenAvailableIsNull_shouldStatus400() {
        ItemDto itemDto = new ItemDto(1L, "name", "desc", null, 1L);

        MockHttpServletRequestBuilder request = post("/items")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, 1L);
        ResponseEntity<Object> response = ResponseEntity.ok(itemDto);
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(response);

        MockHttpServletRequestBuilder request = patch("/items/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, 1L);
        ResponseEntity<Object> response = ResponseEntity.ok(itemDto);
        when(itemClient.getItemById(anyLong(), anyLong())).thenReturn(response);

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
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, 1L);
        ItemDto itemDto2 = new ItemDto(2L, "name2", "desc2", true, null);
        List<ItemDto> items = List.of(itemDto, itemDto2);
        ResponseEntity<Object> response = ResponseEntity.ok(items);
        when(itemClient.getItemsByTheOwner(anyLong(), anyInt(), anyInt())).thenReturn(response);

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
    void getItemsByTheOwnerTest_whenTextIsNull_shouldResponseStatus400() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .header("X-Sharer-User-Id", "1")
                .param("from", "0")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "text");
        ResponseEntity<Object> response = ResponseEntity.ok(commentRequestDto);
        when(itemClient.createComment(anyLong(), anyLong(), any(CommentRequestDto.class))).thenReturn(response);

        MockHttpServletRequestBuilder request = post("/items/1/comment")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void createCommentTest_whenTextIsNull_shouldResponseStatus400() {
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "   ");

        MockHttpServletRequestBuilder request = post("/items/1/comment")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }
}