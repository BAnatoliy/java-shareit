package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderById(Long id);

    List<Item> findByOwnerIdOrderById(Long id, Pageable pageable);

    @Query("select i from Item i where i.available is true and " +
            "(lower(i.description) like lower(concat('%', :text, '%')) or " +
            "lower(i.name) like lower(concat('%', :text, '%'))) order by i.id")
    List<Item> findAvailableItemByNameAndDescription(@Param(value = "text") String text);

    @Query(value = "select * from items as i where i.available is true and " +
            "(lower(i.description) like lower(concat('%', :text, '%')) or " +
            "lower(i.name) like lower(concat('%', :text, '%'))) order by i.id limit :size offset :from",
            nativeQuery = true)
    List<Item> findAvailableItemByNameAndDescription(@Param(value = "text") String text,
                                                     @Param(value = "from") Integer from,
                                                     @Param(value = "size") Integer size);
}
