import aidyn.kelbetov.dto.AuthResponse;
import aidyn.kelbetov.dto.LoginRequest;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.jwt.JwtTokenProvider;
import aidyn.kelbetov.model.Role;
import aidyn.kelbetov.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceImplTest {
    private RestTemplate restTemplate;
    private JwtTokenProvider jwtTokenProvider;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authService = new AuthServiceImpl(restTemplate, jwtTokenProvider);
    }

    @Test
    void login_ValidConfirmedUser_ReturnsToken() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setRole(Role.ROLE_STUDENT);
        userDto.setEmailConfirmed(true);

        when(restTemplate.getForEntity(contains("/by-email?email="), eq(UserDto.class)))
                .thenReturn(ResponseEntity.ok(userDto));
        when(jwtTokenProvider.generateToken(userDto)).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Role.ROLE_STUDENT, response.getRole());
    }

    @Test
    void login_UnconfirmedEmail_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setRole(Role.ROLE_STUDENT);
        userDto.setEmailConfirmed(false);

        when(restTemplate.getForEntity(contains("/by-email?email="), eq(UserDto.class)))
                .thenReturn(ResponseEntity.ok(userDto));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(request));

        assertEquals("Email не подтвержден или пользователь не найден", exception.getMessage());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password");

        when(restTemplate.getForEntity(anyString(), eq(UserDto.class)))
                .thenReturn(ResponseEntity.ok(null)); // или .thenThrow(...)

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.login(request));

        assertEquals("Email не подтвержден или пользователь не найден", exception.getMessage());
    }

}
