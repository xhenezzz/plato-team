package aidyn.kelbetov.dto;

import aidyn.kelbetov.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    @Email
    private String email;
    @NotBlank
    private String password;
}
