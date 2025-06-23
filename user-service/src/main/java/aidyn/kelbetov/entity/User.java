package aidyn.kelbetov.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
public class User {
    @NotBlank(message = "Имя не может быть пустым! Пожалуйста заполните поле!")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустым! Пожалуйста заполните поле!")
    private String surname;
    @Email
    private String email;
    @NotBlank(message = "Пароль не может быть пустым! Пожалуйста заполните поле!")
    @Size(min = 6, message = "Пароль дожен содержать минимум 6 символов!")
    private String password;
    private Role role = Role.ROLE_STUDENT; //Дефолтное значение при регистрации;
    private boolean emailConfirmed = false;
    private String confirmationToken;
    private LocalDateTime tokenExpiry;
}
