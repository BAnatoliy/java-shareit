package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.valid.UserValidGroups;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Min(1)
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotEmpty(groups = UserValidGroups.OnCreate.class)
    @Email(groups = {UserValidGroups.OnCreate.class, UserValidGroups.OnUpdate.class})
    private String email;
    @Size(min = 4, groups = UserValidGroups.OnCreate.class)
    @NotBlank(groups = UserValidGroups.OnCreate.class)
    @NotNull(groups = UserValidGroups.OnCreate.class)
    private String name;
}
