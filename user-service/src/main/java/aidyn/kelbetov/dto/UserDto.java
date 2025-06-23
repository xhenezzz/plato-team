package aidyn.kelbetov.dto;

import aidyn.kelbetov.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {
    @NotBlank(message = "Имя не может быть пустым! Пожалуйста заполните поля!")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустым! Пожалуйста заполните поля!")
    private String surname;
    @Email
    private String email;
    private Role role;
}
