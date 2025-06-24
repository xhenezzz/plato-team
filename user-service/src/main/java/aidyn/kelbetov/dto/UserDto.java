package aidyn.kelbetov.dto;

import aidyn.kelbetov.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Имя не может быть пустым! Пожалуйста заполните поля!")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустым! Пожалуйста заполните поля!")
    private String surname;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    private Role role;
    private boolean emailConfirmed;
}
