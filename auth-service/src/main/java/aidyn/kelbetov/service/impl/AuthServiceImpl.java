package aidyn.kelbetov.service.impl;

import aidyn.kelbetov.dto.AuthResponse;
import aidyn.kelbetov.dto.LoginRequest;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.jwt.JwtTokenProvider;
import aidyn.kelbetov.model.Role;
import aidyn.kelbetov.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {
    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(RestTemplate restTemplate, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Value("${user-service.url}")
    private String userServiceUrl; // например: http://localhost:8081

    @Override
    public AuthResponse login(LoginRequest request) {
        String url = userServiceUrl + "/api/users/by-email?email=" + request.getEmail();
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        UserDto user = response.getBody();

        if (user == null || !user.isEmailConfirmed()) {
            throw new RuntimeException("Email не подтвержден или пользователь не найден");
        }

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}
