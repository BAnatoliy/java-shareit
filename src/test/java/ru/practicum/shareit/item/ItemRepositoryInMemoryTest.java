package ru.practicum.shareit.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRepositoryInMemoryTest {
    private ConfigurableApplicationContext context;
    private final Gson gson = new GsonBuilder().create();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url = "http://localhost:8080/items";
    private final String headerUserId = "X-Sharer-User-Id";
    private final String headerContentType = "Content-Type";
    private final String VALUE_CONTENT_TYPE = "application/json";
    private final String pathSearchWithParameter = "http://localhost:8080/items/search?text=";


    @BeforeEach
    public void init() throws IOException, InterruptedException {
        context = SpringApplication.run(ShareItApp.class);
        createThreeUsers();
    }

    @Test
    @DisplayName("POST создание вещи")
    public void create_Item_Test() throws IOException, InterruptedException {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Дрель электрическая");
        itemDto.setAvailable(false);
        String json1 = gson.toJson(itemDto);
        HttpResponse<String> response1 = sengPostRequest(json1, "1");

        itemDto.setName("Шуруповерт");
        itemDto.setDescription("Шуруповерт аккумуляторный");
        String json2 = gson.toJson(itemDto);
        HttpResponse<String> response2 = sengPostRequest(json2, "1");

        assertAll(() -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(2, getItemsRequest(url, "1").size());
                });

        itemDto.setName("Рубанок");
        itemDto.setDescription("Электрический рубанок Bosh");
        String json3 = gson.toJson(itemDto);
        HttpResponse<String> response3 = sengPostRequest(json3, "111");
        assertEquals(404, response3.statusCode());

        itemDto.setName("");
        String json4 = gson.toJson(itemDto);
        HttpResponse<String> response4 = sengPostRequest(json4, "1");
        assertEquals(400, response4.statusCode());

        itemDto.setName(null);
        String json5 = gson.toJson(itemDto);
        HttpResponse<String> response5 = sengPostRequest(json5, "1");
        assertEquals(400, response5.statusCode());

        itemDto.setName("Рубанок");
        itemDto.setDescription(null);
        String json6 = gson.toJson(itemDto);
        HttpResponse<String> response6 = sengPostRequest(json6, "1");
        assertEquals(400, response6.statusCode());

        assertEquals(2, getItemsRequest(url, "1").size());
    }

    @Test
    @DisplayName("PATCH обновление вещи")
    public void update_Item_Test() throws IOException, InterruptedException {
        create_Two_Items_For_User_Id1_And_One_Item_For_User_Id2();
        ItemDto itemDto = new ItemDto();

        itemDto.setName("Фен строительный");
        String json1 = gson.toJson(itemDto);
        assertNotEquals("Фен строительный", getItemById("1", "1").getName());
        HttpResponse<String> response1 = sengPatchRequest(json1, "1", "1");
        assertEquals(200, response1.statusCode());
        assertEquals("Фен строительный", getItemById("1", "1").getName());

        itemDto.setAvailable(true);
        String json2 = gson.toJson(itemDto);
        assertFalse(getItemById("1", "1").getAvailable());
        HttpResponse<String> response2 = sengPatchRequest(json2, "1", "1");
        assertEquals(200, response2.statusCode());
        assertTrue(getItemById("1", "1").getAvailable());

        itemDto.setDescription("Фен строительный Bosh 2 кВт");
        String json3 = gson.toJson(itemDto);
        assertNotEquals("Фен строительный Bosh 2 кВт", getItemById("1", "1").getDescription());
        HttpResponse<String> response3 = sengPatchRequest(json3, "1", "1");
        assertEquals(200, response3.statusCode());
        assertEquals("Фен строительный Bosh 2 кВт", getItemById("1", "1").getDescription());

        HttpResponse<String> response4 = sengPatchRequest(json3, "1", "111");
        assertEquals(404, response4.statusCode());

        HttpResponse<String> response5 = sengPatchRequest(json3, "111", "1");
        assertEquals(404, response5.statusCode());

        HttpResponse<String> responseForOtherUser = sengPatchRequest(json3, "1", "2");
        assertEquals(404, responseForOtherUser.statusCode());
    }

    @Test
    @DisplayName("GET получение вещи по ID")
    public void get_Item_By_Id_Test() throws IOException, InterruptedException {
        create_Two_Items_For_User_Id1_And_One_Item_For_User_Id2();

        ItemDto itemDto1 = getItemById("1", "1");
        assertEquals(1, itemDto1.getId());

        ItemDto itemDto2 = getItemById("3", "2");
        assertEquals(3, itemDto2.getId());

        HttpRequest requestWithWrongUserId = HttpRequest.newBuilder().uri(URI.create(url + "/" + "333"))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, "1").GET().build();
        HttpResponse<String> responseWithWrongUserId = client.send(requestWithWrongUserId,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseWithWrongUserId.statusCode());
    }

    @Test
    @DisplayName("GET получение списка своих вещей владельцем")
    public void get_The_Owners_Items() throws IOException, InterruptedException {
        create_Two_Items_For_User_Id1_And_One_Item_For_User_Id2();

        assertEquals(2, getItemsRequest(url, "1").size());
        assertEquals(1, getItemsRequest(url, "2").size());

        HttpRequest requestWithWrongUserId = HttpRequest.newBuilder().uri(URI.create(url))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, "111").GET().build();
        HttpResponse<String> responseWithWrongUserId = client.send(requestWithWrongUserId,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseWithWrongUserId.statusCode());
    }

    @Test
    @DisplayName("GET поиск вещей по названию и описанию вещи")
    public void get_Available_Item_Test() throws IOException, InterruptedException {
        create_Two_Items_For_User_Id1_And_One_Item_For_User_Id2();

        List<ItemDto> itemsRequest1 = getItemsRequest(pathSearchWithParameter + "рель", "1");
        assertEquals(0, itemsRequest1.size());

        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        String json = gson.toJson(itemDto);
        sengPatchRequest(json, "1", "1");
        sengPatchRequest(json, "2", "1");
        sengPatchRequest(json, "3", "2");

        List<ItemDto> itemsRequest2 = getItemsRequest(pathSearchWithParameter + "рель", "1");
        assertEquals(1, itemsRequest2.size());

        List<ItemDto> itemsRequest3 = getItemsRequest(pathSearchWithParameter + "ру", "1");
        assertAll(() -> {
            assertEquals(2, itemsRequest3.size());
            assertTrue(itemsRequest3.get(0).getName().toLowerCase().contains("ру"));
            assertTrue(itemsRequest3.get(1).getName().toLowerCase().contains("ру"));
        });
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }

    private HttpResponse<String> sengPostRequest(String json, String userId) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, userId).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sengPatchRequest(String json, String itemId, String userId) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/" + itemId))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, userId).method("PATCH", body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private ItemDto getItemById(String itemId, String userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/" + itemId))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, userId).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), ItemDto.class);
    }

    private List<ItemDto> getItemsRequest(String url, String userId) throws IOException, InterruptedException {
        Type type = new TypeToken<List<ItemDto>>() {}.getType();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header(headerContentType, VALUE_CONTENT_TYPE)
                .header(headerUserId, userId).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), type);
    }

    private void createUserRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/users"))
                .header(headerContentType, VALUE_CONTENT_TYPE).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createThreeUsers() throws IOException, InterruptedException {
        UserDto userDto = new UserDto();
        userDto.setEmail("qw@as.ru");
        userDto.setName("Pablo");
        createUserRequest(gson.toJson(userDto));

        userDto.setEmail("zx@gh.com");
        userDto.setName("Tonny");
        createUserRequest(gson.toJson(userDto));

        userDto.setEmail("tu@by.com");
        userDto.setName("Ronny");
        createUserRequest(gson.toJson(userDto));
    }

    private void create_Two_Items_For_User_Id1_And_One_Item_For_User_Id2() throws IOException, InterruptedException {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Дрель электрическая");
        itemDto.setAvailable(false);
        String json1 = gson.toJson(itemDto);
        sengPostRequest(json1, "1");

        itemDto.setName("Шуруповерт");
        itemDto.setDescription("Шуруповерт аккумуляторный");
        String json2 = gson.toJson(itemDto);
        sengPostRequest(json2, "1");

        itemDto.setName("Рубанок");
        itemDto.setDescription("Электрический рубанок Bosh");
        String json3 = gson.toJson(itemDto);
        sengPostRequest(json3, "2");
    }
}
