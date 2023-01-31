package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSlimDto;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappersTest {
    ItemMapperImpl itemMapper = new ItemMapperImpl();
    UserMapperImpl userMapper = new UserMapperImpl();
    BookingMapperImpl bookingMapper = new BookingMapperImpl();

    @Test
    void userMapperTest() {
        UserDto userDto = new UserDto(1L, "user", "e@a.rt", Set.of(new ItemSlimDto(
                        1L, "name", "desc", true),
                new ItemSlimDto(2L, "name", "desc", true)), Set.of(new BookingSlimDto(
                1L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING, 5L)));
        User user = userMapper.mapToUser(userDto);
        Item item = new Item(1L, "item", "desc", true, user, null,
                null, null, null, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING, user, item);
        item.setLastBooking(booking);
        item.setNextBooking(booking);
        Comment comment = new Comment(1L, "text", LocalDateTime.now(), user, item);
        ItemRequest itemRequest = new ItemRequest(1L, "desc", LocalDateTime.now(), Set.of(item), user);
        item.setComments(Set.of(comment));
        item.setRequest(itemRequest);

        UserDto userDto1 = userMapper.mapToUserDto(user);
        assertEquals(userDto.getId(), userDto1.getId());
        assertEquals(userDto.getName(), userDto1.getName());

        List<UserDto> userDtoList = userMapper.mapToListDto(List.of(user));
        assertEquals(1, userDtoList.size());

        ItemDto itemDtoResult = itemMapper.mapToDto(item);
        Item itemResult = itemMapper.mapToItem(itemDtoResult);
        assertEquals(item.getName(), itemResult.getName());

        List<ItemDto> itemDtoList = itemMapper.mapToListDto(List.of(item));
        assertEquals(1, itemDtoList.size());

        CommentDto commentDtoResult = itemMapper.mapToCommentDto(comment);
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "text");
        Comment commentResult = itemMapper.mapToComment(commentRequestDto);
        assertEquals(commentDtoResult.getText(), comment.getText());
        assertEquals(commentResult.getText(), commentRequestDto.getText());

        Long userIdResult = itemMapper.idFromBooker(user);
        assertEquals(1L, userIdResult);

        BookingSlimDto bookingSlimDtoResult = itemMapper.mapToSlimDto(booking);
        String userNameResult = itemMapper.nameFromAuthor(user);
        assertEquals(booking.getId(), bookingSlimDtoResult.getId());
        assertEquals(user.getName(), userNameResult);

        BookingDto bookingDtoResult = bookingMapper.mapToDto(booking);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, 1L, LocalDateTime.now(),
                LocalDateTime.now(), BookingStatus.WAITING);
        Booking bookingResult = bookingMapper.mapToBooking(bookingRequestDto);
        assertEquals(booking.getId(), bookingDtoResult.getId());
        assertEquals(bookingRequestDto.getId(), bookingResult.getId());

        List<BookingDto> bookingDtoList = bookingMapper.mapToListDto(List.of(booking));
        assertEquals(1, bookingDtoList.size());
    }
}
