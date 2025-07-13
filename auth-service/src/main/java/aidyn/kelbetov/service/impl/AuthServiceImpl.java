package aidyn.kelbetov.service.impl;

import aidyn.kelbetov.dto.AuthResponse;
import aidyn.kelbetov.dto.LoginRequest;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.jwt.JwtTokenProvider;
import aidyn.kelbetov.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {
    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${user-service.secret}")
    private String secret;

    public AuthServiceImpl(RestTemplate restTemplate, JwtTokenProvider jwtTokenProvider) {
        this.restTemplate = restTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String url = "http://user-service/api/users/by-email?email=" + request.getEmail();

        // Заголовок с секретом
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-INTERNAL-KEY", secret);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // GET с заголовком
        ResponseEntity<UserDto> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, UserDto.class);

        UserDto user = response.getBody();

        if (user == null || !user.isEmailConfirmed()) {
            throw new RuntimeException("Email не подтвержден или пользователь не найден");
        }

        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole());
    }
}
