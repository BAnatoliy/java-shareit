package ru.practicum.shareit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestControllersUtils {

    public static byte[] readRequest(String path, String nameFile) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(String.format("src/test/resources/%s/%s", path, nameFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
