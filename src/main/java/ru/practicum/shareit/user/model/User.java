package ru.practicum.shareit.user.model;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String email;
    private String name;
}
