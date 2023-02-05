package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItemIdInTest() {
        List<Comment> listComments = commentRepository.findAllByItem_IdIn(List.of(1L, 5L));
        assertEquals(3, listComments.size());
        assertEquals(1L, listComments.get(0).getId());
        assertEquals(2L, listComments.get(1).getId());
        assertEquals(3L, listComments.get(2).getId());
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItemIdTest() {
        List<Comment> listComments = commentRepository.findAllByItem_Id(1L);
        assertEquals(2, listComments.size());
        assertEquals(1L, listComments.get(0).getId());
        assertEquals(3L, listComments.get(1).getId());
    }
}
