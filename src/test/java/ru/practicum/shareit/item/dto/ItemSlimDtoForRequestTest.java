package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemSlimDtoForRequestTest {
    private ItemSlimDtoForRequest itemSlimDtoForRequest;

    @BeforeEach
    void setUp() {
        itemSlimDtoForRequest = new ItemSlimDtoForRequest(
                1L, "name", "description", true, 2L
        );
    }

    @Test
    void getId() {
        assertEquals(1L, itemSlimDtoForRequest.getId());
    }

    @Test
    void getName() {
        assertEquals("name", itemSlimDtoForRequest.getName());
    }

    @Test
    void getDescription() {
        assertEquals("description", itemSlimDtoForRequest.getDescription());
    }

    @Test
    void getAvailable() {
        assertTrue(itemSlimDtoForRequest.getAvailable());
    }

    @Test
    void getRequestId() {
        assertEquals(2L, itemSlimDtoForRequest.getRequestId());
    }

    @Test
    void setId() {
        ItemSlimDtoForRequest itemSlimDtoForRequest2 = new ItemSlimDtoForRequest();
        itemSlimDtoForRequest2.setRequestId(11L);
        assertEquals(11L, itemSlimDtoForRequest2.getRequestId());
    }

    @Test
    void setName() {
        itemSlimDtoForRequest.setName("new name");
        assertEquals("new name", itemSlimDtoForRequest.getName());
    }

    @Test
    void setDescription() {
        itemSlimDtoForRequest.setDescription("new description");
        assertEquals("new description", itemSlimDtoForRequest.getDescription());
    }

    @Test
    void setAvailable() {
        itemSlimDtoForRequest.setAvailable(false);
        assertFalse(itemSlimDtoForRequest.getAvailable());
    }

    @Test
    void setRequestId() {
        itemSlimDtoForRequest.setRequestId(15L);
        assertEquals(15L, itemSlimDtoForRequest.getRequestId());
    }
}