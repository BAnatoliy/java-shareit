package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByUserIdNotOrderByCreatedDesc() {
        List<ItemRequest> listItemRequests = itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(1L);
        assertEquals(5, listItemRequests.size());
        assertTrue(listItemRequests.get(0).getCreated().isAfter(listItemRequests.get(1).getCreated()));
        assertTrue(listItemRequests.get(1).getCreated().isAfter(listItemRequests.get(2).getCreated()));
        assertTrue(listItemRequests.get(2).getCreated().isAfter(listItemRequests.get(3).getCreated()));
        assertTrue(listItemRequests.get(3).getCreated().isAfter(listItemRequests.get(4).getCreated()));

        Page<ItemRequest> listItemRequests2 = itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(
                1L, CustomPageRequest.of(2, 3));
        assertEquals(3, listItemRequests2.getContent().size());
        assertTrue(listItemRequests2.getContent().get(0).getCreated().isAfter(listItemRequests2.getContent().get(1).getCreated()));
        assertTrue(listItemRequests2.getContent().get(1).getCreated().isAfter(listItemRequests2.getContent().get(2).getCreated()));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByUserIdOrderByCreatedDescTest() {
        List<ItemRequest> listItemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(5L);
        assertEquals(2, listItemRequests.size());
        assertEquals(5L, listItemRequests.get(0).getUser().getId());
        assertEquals(5L, listItemRequests.get(1).getUser().getId());
        assertTrue(listItemRequests.get(0).getCreated().isAfter(listItemRequests.get(1).getCreated()));
    }
}
