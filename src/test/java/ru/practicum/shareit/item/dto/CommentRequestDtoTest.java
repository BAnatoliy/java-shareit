package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentRequestDtoTest {
    private CommentRequestDto commentRequestDto;
    @BeforeEach
    void setUp() {
        commentRequestDto = new CommentRequestDto();
        commentRequestDto.setId(1L);
        commentRequestDto.setText("text");
    }

    @Test
    void getId() {
        assertEquals(1L, commentRequestDto.getId());
    }

    @Test
    void getText() {
        assertEquals("text", commentRequestDto.getText());
    }

    @Test
    void setId() {
        commentRequestDto.setId(2L);
        assertEquals(2L, commentRequestDto.getId());
    }

    @Test
    void setText() {
        commentRequestDto.setText("new text");
        assertEquals("new text", commentRequestDto.getText());
    }
}