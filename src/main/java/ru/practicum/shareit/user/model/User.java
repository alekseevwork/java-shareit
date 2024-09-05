package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
    long id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
}
