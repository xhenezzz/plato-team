package aidyn.kelbetov.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    private String name;
    private String surname;
    @Email
    private String email;
    @NotBlank
    private String password;
}
