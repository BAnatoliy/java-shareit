package ru.practicum.shareit.user;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private Long Id;
    private String email;
    private String name;
}
