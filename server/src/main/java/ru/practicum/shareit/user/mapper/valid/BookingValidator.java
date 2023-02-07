/*
package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingValidator implements ConstraintValidator<BookingValid, BookingRequestDto> {
    private static final String ERROR_END_EARLIER_START = "End cannot be earlier than start";
    private static final String ERROR_START_IN_PAST = "Start cannot be in the past";

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext context) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_END_EARLIER_START).addConstraintViolation();
            return false;
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_START_IN_PAST).addConstraintViolation();
            return false;
        }
        return true;
    }
}
*/
