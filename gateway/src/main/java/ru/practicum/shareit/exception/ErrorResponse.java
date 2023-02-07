package ru.practicum.shareit.exception;

public class ErrorResponse {
    private final String error;
    private final String massage;

    public ErrorResponse(String error, String massage) {
        this.error = error;
        this.massage = massage;
    }

    public String getError() {
        return error;
    }

    public String getMassage() {
        return massage;
    }
}
