package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ErrorHandler errorHandler;
    @Autowired
    private UserController userController;
    @MockBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @SneakyThrows
    @Test
    void getUserByIdTest() {
        UserDto user = new UserDto(1L, "us@ya.ru","user");
        ResponseEntity<Object> response = ResponseEntity.ok(user);
        when(userClient.getUserById(1L)).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/users/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        UserDto user = new UserDto(1L, "us@ya.ru","user");
        UserDto user2 = new UserDto(2L,"us2@ya.ru", "user2");
        ResponseEntity<Object> response = ResponseEntity.ok(List.of(user, user2));
        when(userClient.getAllUsers()).thenReturn(response);

        MockHttpServletRequestBuilder request = get("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        UserDto user = new UserDto(1L, "us@ya.ru","user");
        ResponseEntity<Object> response = ResponseEntity.ok(user);
        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(response);

        MockHttpServletRequestBuilder request = patch("/users/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1L), Long.class));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenEmptyName_shouldGetStatus400() {
        UserDto user = new UserDto(1L, "us@ya.ru","   ");
        MockHttpServletRequestBuilder request = post("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenNameNull_shouldGetStatus400() {
        UserDto user = new UserDto(1L, "us@ya.ru",null);
        MockHttpServletRequestBuilder request = post("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenNameSizeIsLess_shouldGetStatus400() {
        UserDto user = new UserDto(1L, "us@ya.ru","nam");
        MockHttpServletRequestBuilder request = post("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenEmailIsNull_shouldGetStatus400() {
        UserDto user = new UserDto(1L, null,"name");
        MockHttpServletRequestBuilder request = post("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenEmailIsInvalid_shouldGetStatus400() {
        UserDto user = new UserDto(1L, "usya.ru","user");
        MockHttpServletRequestBuilder request = post("/users")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void deleteUserTest() {
        doNothing().when(userClient).deleteUser(2L);

        MockHttpServletRequestBuilder request = delete("/users/2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void deleteUserTest_whenThrowValidationException_getStatusResponse409() {
        doThrow(ValidationException.class).when(userClient).deleteUser(2L);

        MockHttpServletRequestBuilder request = delete("/users/2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(409));
    }
}