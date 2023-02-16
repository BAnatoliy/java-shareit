package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void findById() {
        Optional<Item> item1 = itemRepository.findById(1L);
        assertThat(item1).isPresent().hasValueSatisfying(item -> {
            assertThat(item)
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .hasFieldOrPropertyWithValue("name", "item1")
                    .hasFieldOrPropertyWithValue("description", "item description1")
                    .hasFieldOrPropertyWithValue("available", true);
            assertThat(item.getOwner())
                    .hasFieldOrPropertyWithValue("id", 1L)
                    .hasFieldOrPropertyWithValue("name", "user1")
                    .hasFieldOrPropertyWithValue("email", "user1@ya.ru");
            assertEquals(2, item.getBookings().size());
        });

        Optional<Item> item9 = itemRepository.findById(9L);
        assertThat(item9).isPresent().hasValueSatisfying(item -> {
            assertThat(item)
                    .hasFieldOrPropertyWithValue("id", 9L)
                    .hasFieldOrPropertyWithValue("name", "item9")
                    .hasFieldOrPropertyWithValue("description", "item description9")
                    .hasFieldOrPropertyWithValue("available", false);
            assertThat(item.getOwner())
                    .hasFieldOrPropertyWithValue("id", 3L)
                    .hasFieldOrPropertyWithValue("name", "user3")
                    .hasFieldOrPropertyWithValue("email", "user3@ya.ru");
            assertEquals(1, item.getBookings().size());
        });
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void findByOwnerIdOrderByIdTest() {
        List<Item> itemList = itemRepository.findByOwnerIdOrderById(1L);
        assertEquals(4L, itemList.size());
        assertEquals(1L, itemList.get(0).getOwner().getId());
        assertEquals(1L, itemList.get(1).getOwner().getId());
        assertEquals(1L, itemList.get(2).getOwner().getId());
        assertEquals(1L, itemList.get(3).getOwner().getId());

        List<Item> itemList2 = itemRepository.findByOwnerIdOrderById(1L, CustomPageRequest.of(1, 3));
        assertEquals(3, itemList2.size());
        assertEquals(2L, itemList2.get(0).getId());
        assertEquals(3L, itemList2.get(1).getId());
        assertEquals(4L, itemList2.get(2).getId());

        List<Item> itemList3 = itemRepository.findByOwnerIdOrderById(1L, CustomPageRequest.of(2, 1));
        assertEquals(1, itemList3.size());
        assertEquals(3L, itemList3.get(0).getId());
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    void findAvailableItemByNameAndDescription() {
        List<Item> itemList = itemRepository.findAvailableItemByNameAndDescription("dEsc");
        assertEquals(9L, itemList.size());
        assertTrue(itemList.get(0).getDescription().contains("dEsc".toLowerCase()));
        assertTrue(itemList.get(1).getDescription().contains("dEsc".toLowerCase()));
        assertTrue(itemList.get(2).getDescription().contains("dEsc".toLowerCase()));
        assertTrue(itemList.get(8).getDescription().contains("dEsc".toLowerCase()));

        List<Item> itemList2 = itemRepository.findAvailableItemByNameAndDescription("1");
        assertEquals(4, itemList2.size());
        assertTrue(itemList2.get(0).getDescription().contains("1"));
        assertTrue(itemList2.get(1).getDescription().contains("1"));
        assertTrue(itemList2.get(3).getDescription().contains("1"));

        List<Item> itemList3 = itemRepository.findAvailableItemByNameAndDescription("dEsc", 1, 2);
        assertEquals(2, itemList3.size());
        assertEquals(2L, itemList3.get(0).getId());
        assertEquals(4L, itemList3.get(1).getId());
        assertTrue(itemList3.get(0).getDescription().contains("dEsc".toLowerCase()));
        assertTrue(itemList3.get(1).getDescription().contains("dEsc".toLowerCase()));
    }
}