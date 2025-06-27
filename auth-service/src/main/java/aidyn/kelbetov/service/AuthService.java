package aidyn.kelbetov.service;

import aidyn.kelbetov.dto.AuthResponse;
import aidyn.kelbetov.dto.LoginRequest;

public interface AuthService {
    public AuthResponse login(LoginRequest login);
}
