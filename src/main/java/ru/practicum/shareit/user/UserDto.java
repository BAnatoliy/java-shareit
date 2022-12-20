package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    @Min(1)
    @EqualsAndHashCode.Exclude
    private Long Id;
    @NotNull(groups = UserValidGroups.OnCreate.class)
    @Email(groups = {UserValidGroups.OnCreate.class, UserValidGroups.OnUpdate.class})
    private String email;
    @Size(min = 4, groups = UserValidGroups.OnCreate.class)
    @NotBlank(groups = UserValidGroups.OnCreate.class)
    @NotNull(groups = UserValidGroups.OnCreate.class)
    private String name;
}
