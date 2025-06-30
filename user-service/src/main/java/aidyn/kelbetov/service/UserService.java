package aidyn.kelbetov.service;

import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.entity.Role;
import aidyn.kelbetov.entity.User;

public interface UserService {
    UserDto registerUser(RegisterDto user);

    UserDto findByEmail(String email);

    boolean confirmEmail(String token);

    boolean validatePassword(String email, String password);

    void resendConfirmationEmail(String email);

    void requestEmailChange(String newEmail);

    boolean confirmEmailChange(String token);
}