package ru.practicum.shareit.item;

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
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ErrorHandler;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerIntegrationTest {
    private final ItemController itemController;
    private final ErrorHandler errorHandler;
    public MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Available_Item_When_Parameters_Correct_Should_Get_List_With_Three_Items() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .param("text", "dEsCr")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(9)));

        MockHttpServletRequestBuilder request2 = get("/items/search")
                .param("text", "dEsCr")
                .param("from", "3")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[2].available", is(true)))
                .andExpect(jsonPath("$[0].description", containsString("dEsCr".toLowerCase())))
                .andExpect(jsonPath("$[1].description", containsString("dEsCr".toLowerCase())))
                .andExpect(jsonPath("$[2].description",containsString("dEsCr".toLowerCase())));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Available_Item_When_Parameters_Correct_And_Text_Is_Not_Contain_Should_Get_Empty_List() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .param("text", "qwer")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));

        MockHttpServletRequestBuilder request2 = get("/items/search")
                .param("text", "qwer")
                .param("from", "0")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Available_Item_When_Text_Is_Empty_Should_Get_Empty_List() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .param("text", "")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));

        MockHttpServletRequestBuilder request2 = get("/items/search")
                .param("text", "")
                .param("from", "0")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));

        MockHttpServletRequestBuilder request3 = get("/items/search")
                .param("text", "   ")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request3)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));

        MockHttpServletRequestBuilder request4 = get("/items/search")
                .param("text", "   ")
                .param("from", "0")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request4)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Available_Item_When_Parameters_From_And_Size_Is_Not_Correct_Should_Get_Status_400() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .param("text", "dEsCr")
                .param("from", "-1")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(400));

        MockHttpServletRequestBuilder request2 = get("/items/search")
                .param("text", "dEsCr")
                .param("from", "1")
                .param("size", "0")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(400));

        MockHttpServletRequestBuilder request3 = get("/items/search")
                .param("text", "dEsCr")
                .param("from", "1")
                .param("size", "-1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request3)
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Available_Item_When_Parameters_From_Or_Size_Is_Null_Correct_Should_Get_List_With_All_9_Items() {
        MockHttpServletRequestBuilder request = get("/items/search")
                .param("text", "dEsCr")
                .param("size", "3")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(9)));

        MockHttpServletRequestBuilder request2 = get("/items/search")
                .param("text", "dEsCr")
                .param("from", "1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(9)));
    }
}
