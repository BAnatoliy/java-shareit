package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByBooker_IdOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdOrderByStartDesc(5L);
        assertEquals(3, bookingList.size());
        assertEquals(5L, bookingList.get(0).getBooker().getId());
        assertEquals(5L, bookingList.get(1).getBooker().getId());
        assertEquals(5L, bookingList.get(2).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));
        assertTrue(bookingList.get(1).getStart().isAfter(bookingList.get(2).getStart()));

        List<Booking> bookingList2 = bookingRepository.findAllByBooker_IdOrderByStartDesc(
                5L, CustomPageRequest.of(1, 2));
        assertEquals(2, bookingList2.size());
        assertEquals(5L, bookingList2.get(0).getBooker().getId());
        assertEquals(5L, bookingList2.get(1).getBooker().getId());
        assertTrue(bookingList2.get(0).getStart().isAfter(bookingList2.get(1).getStart()));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByBooker_IdAndEndBeforeOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                5L, LocalDateTime.now());
        assertEquals(2, bookingList.size());
        assertEquals(5L, bookingList.get(0).getBooker().getId());
        assertEquals(5L, bookingList.get(1).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));

        List<Booking> bookingList2 = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                5L, LocalDateTime.now(), CustomPageRequest.of(0, 2));
        assertEquals(2, bookingList2.size());
        assertEquals(5L, bookingList2.get(0).getBooker().getId());
        assertEquals(5L, bookingList2.get(1).getBooker().getId());
        assertTrue(bookingList2.get(0).getStart().isAfter(bookingList2.get(1).getStart()));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDescTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                3L, now, now);
        assertEquals(2, bookingList.size());
        assertEquals(3L, bookingList.get(0).getBooker().getId());
        assertEquals(3L, bookingList.get(1).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));
        assertTrue(bookingList.get(0).getStart().isBefore(now));
        assertTrue(bookingList.get(0).getEnd().isAfter(now));
        assertTrue(bookingList.get(1).getStart().isBefore(now));
        assertTrue(bookingList.get(1).getEnd().isAfter(now));

        List<Booking> bookingList2 = bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                3L, now, now, CustomPageRequest.of(1, 2));
        assertEquals(1, bookingList2.size());
        assertEquals(3L, bookingList2.get(0).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isBefore(now));
        assertTrue(bookingList.get(0).getEnd().isAfter(now));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByBooker_IdAndStartAfterOrderByStartDescTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(4L, now);
        assertEquals(2, bookingList.size());
        assertEquals(4L, bookingList.get(0).getBooker().getId());
        assertEquals(4L, bookingList.get(1).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));
        assertTrue(bookingList.get(0).getStart().isAfter(now));
        assertTrue(bookingList.get(0).getEnd().isAfter(now));
        assertTrue(bookingList.get(1).getStart().isAfter(now));
        assertTrue(bookingList.get(1).getEnd().isAfter(now));

        List<Booking> bookingList2 = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                4L, now, CustomPageRequest.of(1, 2));
        assertEquals(1, bookingList2.size());
        assertEquals(4L, bookingList2.get(0).getBooker().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(now));
        assertTrue(bookingList.get(0).getEnd().isAfter(now));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByBooker_IdAndStatusIsOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.APPROVED);
        assertEquals(3, bookingList.size());
        assertEquals(4L, bookingList.get(0).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingList.get(0).getStatus());
        assertEquals(4L, bookingList.get(1).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingList.get(1).getStatus());
        assertEquals(4L, bookingList.get(2).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingList.get(2).getStatus());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));
        assertTrue(bookingList.get(1).getStart().isAfter(bookingList.get(2).getStart()));

        List<Booking> bookingList2 = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.APPROVED, CustomPageRequest.of(1, 2));
        assertEquals(2, bookingList2.size());
        assertEquals(4L, bookingList2.get(0).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingList2.get(0).getStatus());
        assertEquals(4L, bookingList2.get(1).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookingList2.get(1).getStatus());
        assertTrue(bookingList2.get(0).getStart().isAfter(bookingList2.get(1).getStart()));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItem_Owner_IdAndItem_AvailableOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                1L, true);
        assertEquals(3, bookingList.size());
        assertEquals(1L, bookingList.get(0).getItem().getOwner().getId());
        assertTrue(bookingList.get(0).getItem().getAvailable());
        assertEquals(1L, bookingList.get(1).getItem().getOwner().getId());
        assertTrue(bookingList.get(1).getItem().getAvailable());
        assertEquals(1L, bookingList.get(2).getItem().getOwner().getId());
        assertTrue(bookingList.get(2).getItem().getAvailable());
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));
        assertTrue(bookingList.get(1).getStart().isAfter(bookingList.get(2).getStart()));

        List<Booking> bookingList2 = bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                1L, true, CustomPageRequest.of(1, 2));
        assertEquals(2, bookingList2.size());
        assertEquals(1L, bookingList2.get(0).getItem().getOwner().getId());
        assertTrue(bookingList2.get(0).getItem().getAvailable());
        assertEquals(1L, bookingList2.get(1).getItem().getOwner().getId());
        assertTrue(bookingList2.get(1).getItem().getAvailable());
        assertTrue(bookingList2.get(0).getStart().isAfter(bookingList2.get(1).getStart()));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItemIdTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_Id(1L);
        assertEquals(2, bookingList.size());
        assertEquals(1L, bookingList.get(0).getItem().getId());
        assertEquals(1L, bookingList.get(1).getItem().getId());
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDescTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(1L, true, now);
        assertEquals(2, bookingList.size());
        assertEquals(1L, bookingList.get(0).getItem().getOwner().getId());
        assertEquals(1L, bookingList.get(1).getItem().getOwner().getId());
        assertTrue(bookingList.get(0).getEnd().isBefore(now));
        assertTrue(bookingList.get(1).getEnd().isBefore(now));
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));

        List<Booking> bookingList2 = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
                        1L, true, now, CustomPageRequest.of(1, 2));
        assertEquals(1, bookingList2.size());
        assertEquals(1L, bookingList2.get(0).getItem().getOwner().getId());
        assertTrue(bookingList2.get(0).getEnd().isBefore(now));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDescTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(2L, true, now);
        assertEquals(2, bookingList.size());
        assertEquals(2L, bookingList.get(0).getItem().getOwner().getId());
        assertEquals(2L, bookingList.get(1).getItem().getOwner().getId());
        assertTrue(bookingList.get(0).getStart().isAfter(now));
        assertTrue(bookingList.get(1).getStart().isAfter(now));
        assertTrue(bookingList.get(0).getStart().isAfter(bookingList.get(1).getStart()));

        List<Booking> bookingList2 = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
                        2L, true, now, CustomPageRequest.of(1, 2));
        assertEquals(1, bookingList2.size());
        assertEquals(2L, bookingList2.get(0).getItem().getOwner().getId());
        assertTrue(bookingList2.get(0).getStart().isAfter(now));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDescTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                        4L, true, now, now);
        assertEquals(1, bookingList.size());
        assertEquals(4L, bookingList.get(0).getItem().getOwner().getId());
        assertTrue(bookingList.get(0).getStart().isBefore(now));
        assertTrue(bookingList.get(0).getEnd().isAfter(now));

        List<Booking> bookingList2 = bookingRepository
                .findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                        4L, true, now, now, CustomPageRequest.of(0, 3));
        assertEquals(1, bookingList2.size());
        assertEquals(4L, bookingList2.get(0).getItem().getOwner().getId());
        assertTrue(bookingList2.get(0).getStart().isBefore(now));
        assertTrue(bookingList2.get(0).getEnd().isAfter(now));
    }

    @Test
    @Sql(value = {"classpath:sql/schemaH2.sql", "classpath:sql/insert_Items.sql"})
    public void findAllByItem_Owner_IdAndStatusIsOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository
                .findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(2L, BookingStatus.WAITING);
        assertEquals(1, bookingList.size());
        assertEquals(2L, bookingList.get(0).getItem().getOwner().getId());
        assertEquals(BookingStatus.WAITING, bookingList.get(0).getStatus());

        List<Booking> bookingList2 = bookingRepository
                .findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(2L, BookingStatus.REJECTED,
                        CustomPageRequest.of(0, 3));
        assertEquals(1, bookingList2.size());
        assertEquals(2L, bookingList2.get(0).getItem().getOwner().getId());
        assertEquals(BookingStatus.REJECTED, bookingList2.get(0).getStatus());
    }
}
