package aidyn.kelbetov.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Имя не может быть пустым! Пожалуйста заполните поле!")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустым! Пожалуйста заполните поле!")
    private String surname;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    private String password;
    private Role role;
    private boolean emailConfirmed = false;
    private String confirmationToken;
    private LocalDateTime tokenExpiry;
}
