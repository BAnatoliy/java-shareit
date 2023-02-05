package ru.practicum.shareit.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    BookingDto mapToDto(Booking booking);

    Booking mapToBooking(BookingRequestDto bookingRequestDto);

    List<BookingDto> mapToListDto(List<Booking> bookingList);
}
