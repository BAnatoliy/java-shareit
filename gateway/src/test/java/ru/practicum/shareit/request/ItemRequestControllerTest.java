package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ErrorHandler errorHandler;
    @Autowired
    private ItemRequestController itemRequestController;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @SneakyThrows
    @Test
    void createTest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc");
        ResponseEntity<Object> response = ResponseEntity.ok(itemRequestDto);
        when(itemRequestClient.create(anyLong(), any(ItemRequestDto.class))).thenReturn(response);

        MockHttpServletRequestBuilder request = post("/requests")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void createTest_whenDescriptionIsEmpty_shouldGetStatus400() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "   ");
        MockHttpServletRequestBuilder request = post("/requests")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void getUsersRequests() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc");
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(itemRequestDto));
        when(itemRequestClient.getUsersRequests(anyLong())).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/requests")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc");
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(itemRequestDto));
        when(itemRequestClient.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/requests/all")
                .header("X-Sharer-User-Id", "1")
                .param("from", "0")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc");
        ResponseEntity<Object> response = ResponseEntity.ok(itemRequestDto);
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong())).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/requests/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }
}