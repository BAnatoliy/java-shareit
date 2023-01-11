package ru.practicum.shareit.item.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {
    ItemDto mapToDto(Item item);

    Item mapToItem(ItemDto itemDto);

    @Mapping(target = "bookerId", source = "booker", qualifiedByName = "idFromBooker")
    BookingSlimDto mapToSlimDto(Booking booking);

    Comment mapToComment(CommentRequestDto commentRequestDto);

    @Mapping(target = "authorName", source = "author", qualifiedByName = "nameFromAuthor")
    CommentDto mapToCommentDto(Comment comment);

    @Named("idFromBooker")
    default Long idFromBooker(User booker) {
        return booker.getId();
    }

    @Named("nameFromAuthor")
    default String nameFromAuthor(User author) {
        return author.getName();
    }
}
