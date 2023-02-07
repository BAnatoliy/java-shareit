package ru.practicum.shareit.pageableImpl;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomPageRequestTest {

    @Test
    void customPageRequestTest() {
        CustomPageRequest customPageRequest = CustomPageRequest.of(
                10, 2);
        int pageNumber = customPageRequest.getPageNumber();
        assertEquals(10 / 2, pageNumber);

        Pageable next = customPageRequest.next();
        assertEquals(customPageRequest.getPageNumber() + 1, next.getOffset());

        Pageable pageable = customPageRequest.previousOrFirst();
        assertEquals(0, pageable.getOffset());

        Pageable pageable2 = customPageRequest.first();
        assertEquals(0, pageable2.getOffset());

        Pageable pageable3 = customPageRequest.withPage(1);
        assertEquals(2, pageable3.getOffset());

        Pageable pageable4 = customPageRequest.previous();
        assertEquals(9, pageable4.getOffset());
    }
}