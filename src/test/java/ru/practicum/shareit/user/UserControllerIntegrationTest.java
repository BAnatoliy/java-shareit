package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestControllersUtils.readRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerIntegrationTest {
    private final UserController userController;
    private final ErrorHandler errorHandler;
    private final UserRepository userRepository;
    public MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void create_User_When_Email_Not_Busy_Should_User_Will_Created() {
        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readRequest("json", "user_Jack.json"));

        mockMvc.perform(request)
                .andExpect(jsonPath("$.id", is(6L), Long.class))
                .andExpect(jsonPath("$.name", is("jack")))
                .andExpect(jsonPath("$.email", is("jack@ya.ru")));

        Optional<User> user6 = userRepository.findById(6L);
        assertThat(user6).isPresent().hasValueSatisfying(user -> assertThat(user)
                .hasFieldOrPropertyWithValue("id", 6L)
                .hasFieldOrPropertyWithValue("name", "jack")
                .hasFieldOrPropertyWithValue("email", "jack@ya.ru"));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void create_User_When_Email_Busy_Should_User_Will_Not_Created() {
        Optional<User> user1 = userRepository.findById(1L);
        assertThat(user1).isPresent().hasValueSatisfying(user -> assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "user1")
                .hasFieldOrPropertyWithValue("email", "user1@ya.ru"));

        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readRequest("json", "user_User1.json"));

        mockMvc.perform(request)
                .andExpect(status().is(500));
    }
}