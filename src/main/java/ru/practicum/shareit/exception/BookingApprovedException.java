package ru.practicum.shareit.exception;

public class BookingApprovedException extends RuntimeException{
    public BookingApprovedException(String message) {
        super(message);
    }
}
