package aidyn.kelbetov.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotBlank(message = "Имя не может быть пустым! Пожалуйста заполните поле!")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустым! Пожалуйста заполните поле!")
    private String surname;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    @NotBlank(message = "Пароль не может быть пустым! Пожалуйста заполните поле!")
    @Size(min = 6, message = "Пароль дожен содержать минимум 6 символов!")
    private String password;
}
