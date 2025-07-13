package aidyn.kelbetov.service;

import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;

public interface UserService {
    UserDto registerUser(RegisterDto user);

    UserDto findByEmail(String email);

    boolean confirmEmail(String token);

    boolean validatePassword(String email, String password);

    void resendConfirmationEmail(String email);

    void requestEmailChange(String currentEmail, String newEmail);

    boolean confirmEmailChange(String token);
}