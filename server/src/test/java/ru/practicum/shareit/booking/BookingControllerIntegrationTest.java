package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exception.ErrorHandler;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerIntegrationTest {
    private final BookingController bookingController;
    private final ErrorHandler errorHandler;
    public MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(errorHandler)
                .build();
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Null_And_Parameters_Correct_Should_Get_List_All_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Past_And_Parameters_Correct_Should_Get_List_Past_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "PAST")
                .param("from", "1")
                .param("size", "1")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "PAST")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Future_And_Parameters_Correct_Should_Get_List_Future_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "FUTURE")
                .param("from", "1")
                .param("size", "1")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "FUTURE")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Current_And_Parameters_Correct_Should_Get_List_Current_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "CURRENT")
                .param("from", "0")
                .param("size", "2")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "CURRENT")
                .header("X-Sharer-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Waiting_And_Parameters_Correct_Should_Get_List_Waiting_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "WAITING")
                .param("from", "0")
                .param("size", "2")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "WAITING")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Rejected_And_Parameters_Correct_Should_Get_List_Rejected_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "REJECTED")
                .param("from", "0")
                .param("size", "2")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "REJECTED")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Not_Correct_And_Parameters_Correct_Should_Get_Empty_List_Bookings() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "UNKNOWN")
                .param("from", "0")
                .param("size", "2")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(400));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "UNKNOWN")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Status_Is_Default_And_Parameters_Is_Not_Correct_Should_Get_Status_404() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "ALL")
                .param("from", "-1")
                .param("size", "2")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(400));

        MockHttpServletRequestBuilder request2 = get("/bookings/owner")
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "0")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request2)
                .andExpect(status().is(400));

        MockHttpServletRequestBuilder request3 = get("/bookings/owner")
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "-1")
                .header("X-Sharer-User-Id", "2")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request3)
                .andExpect(status().is(400));
    }

    @Test
    @SneakyThrows
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void get_Booking_By_Booker_When_Parameters_Is_Correct_And_User_Not_Exist_Should_Get_Status_404() {
        MockHttpServletRequestBuilder request = get("/bookings/owner")
                .param("state", "ALL")
                .param("from", "1")
                .param("size", "2")
                .header("X-Sharer-User-Id", "222")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(404));
    }
}
