package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.BookingApprovedException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ValidationException;
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
    private UserService userService;
    UserDto user;
    UserDto user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(errorHandler)
                .build();
        user = new UserDto(1L, "us@ya.ru","user", null, null);
        user2 = new UserDto(2L,"us2@ya.ru", "user2", null,null);
    }

    @SneakyThrows
    @Test
    void getUserByIdTest() {
        when(userService.getUserById(1L)).thenReturn(user);

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
        List<UserDto> users = List.of(user, user2);
        when(userService.getAllUsers()).thenReturn(users);

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
        when(userService.updateUser(any(UserDto.class), anyLong())).thenReturn(user2);

        MockHttpServletRequestBuilder request = patch("/users/1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2));

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(2L), Long.class));
    }

    @SneakyThrows
    @Test
    void createUserTest_whenEmptyName_shouldGetStatus400() {
        user.setName("   ");
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
        user.setName(null);
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
        user.setName("nam");
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
        user.setEmail(null);
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
        user.setEmail("null.ru");
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
        doNothing().when(userService).deleteUser(2L);

        MockHttpServletRequestBuilder request = delete("/users/2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void deleteUserTest_whenThrowValidationException_getStatusResponse409() {
        doThrow(ValidationException.class).when(userService).deleteUser(2L);

        MockHttpServletRequestBuilder request = delete("/users/2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(409));
    }

    @SneakyThrows
    @Test
    void deleteUserTest_whenThrowBookingApprovedException_getStatusResponse409() {
        doThrow(BookingApprovedException.class).when(userService).deleteUser(2L);

        MockHttpServletRequestBuilder request = delete("/users/2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(400));
    }
}