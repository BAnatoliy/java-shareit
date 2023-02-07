package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserSlimDto {
        private Long id;
        private String email;
        private String name;
    }

