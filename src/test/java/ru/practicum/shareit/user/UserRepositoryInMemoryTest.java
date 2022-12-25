package ru.practicum.shareit.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.UserDto;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@SpringBootApplication
public class UserRepositoryInMemoryTest {
    private ConfigurableApplicationContext context;
    private final Gson gson = new GsonBuilder().create();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url = "http://localhost:8080/users";
    private final String headerContentType = "Content-Type";
    private final String valueContentType = "application/json";

    @BeforeEach
    public void init() {
        context = SpringApplication.run(ShareItApp.class);
    }

    @Test
    @DisplayName("POST создание пользователя")
    public void create_User_Test() throws IOException, InterruptedException {
        UserDto userDto =  new UserDto();
        userDto.setEmail("aq@sd.com");
        userDto.setName("Bean");
        String json = gson.toJson(userDto);
        HttpResponse<String> response = sendPostRequest(json);

        String json1 = gson.toJson(userDto);
        HttpResponse<String> response1 = sendPostRequest(json1);

        userDto.setEmail("aqsd.com");
        userDto.setName("Bean");
        String json2 = gson.toJson(userDto);
        HttpResponse<String> response2 = sendPostRequest(json2);

        userDto.setEmail("aq@sd.com");
        userDto.setName("");
        String json3 = gson.toJson(userDto);
        HttpResponse<String> response3 = sendPostRequest(json3);

        userDto.setName("   ");
        String json4 = gson.toJson(userDto);
        HttpResponse<String> response4 = sendPostRequest(json4);

        userDto.setEmail("");
        userDto.setName("Bean");
        String json5 = gson.toJson(userDto);
        HttpResponse<String> response5 = sendPostRequest(json5);

        userDto.setEmail(null);
        String json6 = gson.toJson(userDto);
        HttpResponse<String> response6 = sendPostRequest(json6);

        assertAll(() -> {
            assertEquals(200, response.statusCode());
            assertEquals(409, response1.statusCode());
            assertEquals(400, response2.statusCode());
            assertEquals(400, response3.statusCode());
            assertEquals(400, response4.statusCode());
            assertEquals(400, response5.statusCode());
            assertEquals(400, response6.statusCode());
            assertEquals(1, getListUsers().size());
        });
    }

    @Test
    @DisplayName("PATCH обновление пользователя")
    public void update_User_Test() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("aq@sd.com");
        userDto.setName("Bean");
        String json1 = gson.toJson(userDto);
        sendPostRequest(json1);
        assertAll(() -> {
        assertEquals(1, getListUsers().size());
        assertEquals("aq@sd.com", getListUsers().get(0).getEmail());
        });

        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate.setEmail("Leon@yandex.ru");
        userDtoToUpdate.setName("Leonard");
        String json2 = gson.toJson(userDtoToUpdate);
        HttpResponse<String> response1 = sendPatchRequest(json2, URI.create(url + "/1"));

        assertAll(() -> {
            assertEquals(200, response1.statusCode());
            assertEquals(1, getListUsers().size());
            assertEquals("Leon@yandex.ru", getListUsers().get(0).getEmail());
        });
    }

    @Test
    @DisplayName("GET получения пользователя по ID")
    public void get_User_By_Id_Test() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("aq@sd.com");
        userDto.setName("Bean");
        String json1 = gson.toJson(userDto);
        sendPostRequest(json1);
        assertEquals(1, getListUsers().size());

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/1"))
                .header(headerContentType, valueContentType).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        UserDto responseUser1 = gson.fromJson(response.body(), UserDto.class);

        HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url + "/2"))
                .header(headerContentType, valueContentType).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertAll(() -> {
            assertEquals(200, response.statusCode());
            assertEquals(1, responseUser1.getId());
            assertEquals("aq@sd.com", responseUser1.getEmail());
            assertEquals("Bean", responseUser1.getName());
            assertEquals(404, response2.statusCode());
        });
    }

    @Test
    @DisplayName("GET получения всех пользователей")
    public void get_All_Users_Test() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("aq@sd.com");
        userDto.setName("Bean");
        String json1 = gson.toJson(userDto);
        sendPostRequest(json1);
        assertEquals(1, getListUsers().size());

        userDto.setEmail("aqsd@gmail.com");
        userDto.setName("Bean1");
        String json2 = gson.toJson(userDto);
        sendPostRequest(json2);
        assertEquals(2, getListUsers().size());
    }

    @Test
    @DisplayName("DELETE удаление пользователя по ID")
    public void delete_User_By_Id_Test() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("aq@sd.com");
        userDto.setName("Bean");
        String json1 = gson.toJson(userDto);
        sendPostRequest(json1);

        userDto.setEmail("aqsd@gmail.com");
        userDto.setName("Bean1");
        String json2 = gson.toJson(userDto);
        sendPostRequest(json2);

        userDto.setEmail("bean@gmail.com");
        userDto.setName("Bean2");
        String json3 = gson.toJson(userDto);
        sendPostRequest(json3);
        assertEquals(3, getListUsers().size());

        HttpResponse<String> response = sendDeleteRequest("/1");
        assertAll(() -> {
            assertEquals(200, response.statusCode());
            assertEquals(2, getListUsers().size());
        });

        HttpResponse<String> response2 = sendDeleteRequest("/2");
        assertAll(() -> {
            assertEquals(200, response2.statusCode());
            assertEquals(1, getListUsers().size());
            assertEquals(3, getListUsers().get(0).getId());
        });

        HttpResponse<String> response3 = sendDeleteRequest("/1111");
        assertAll(() -> {
            assertEquals(404, response3.statusCode());
            assertEquals(1, getListUsers().size());
        });

        HttpResponse<String> response4 = sendDeleteRequest("/3");
        assertAll(() -> {
            assertEquals(200, response4.statusCode());
            assertEquals(0, getListUsers().size());
        });
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }

    private HttpResponse<String> sendPostRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header(headerContentType, valueContentType)
                .POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPatchRequest(String json, URI uri) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header(headerContentType, valueContentType)
                .method("PATCH", body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + id)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<UserDto> getListUsers() throws IOException, InterruptedException {
        Type type = new TypeToken<List<UserDto>>(){}.getType();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header(headerContentType, valueContentType).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), type);
    }
}
