import aidyn.kelbetov.dto.LoginUserDto;
import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.entity.User;
import aidyn.kelbetov.exception.EmailNotConfirmedException;
import aidyn.kelbetov.exception.UserAlreadyExistsException;
import aidyn.kelbetov.exception.UserNotFoundException;
import aidyn.kelbetov.mapper.UserMapper;
import aidyn.kelbetov.repo.UserRepository;
import aidyn.kelbetov.service.EmailService;
import aidyn.kelbetov.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository repository;
    @Mock private UserMapper userMapper;
    @Mock private EmailService emailService;

    @InjectMocks private UserServiceImpl userService;

    private RegisterDto registerDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password123");
        registerDto.setName("John");
        registerDto.setSurname("Doe");

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        userDto = new UserDto();
        userDto.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Регистрация: успех")
    void registerUser_Success() {
        when(repository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.fromRegisterDto(registerDto, passwordEncoder)).thenReturn(user);
        when(repository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.registerUser(registerDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());

        verify(emailService).sendConfirmationEmail(eq("test@example.com"), anyString());
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("Регистрация: уже существует email")
    void registerUser_AlreadyExists_Throws() {
        when(repository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(registerDto));

        verify(repository, never()).save(any());
        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Подтверждение email: валидный токен")
    void confirmEmail_ValidToken_Success() {
        String token = "valid-token";
        user.setConfirmationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setEmailConfirmed(false);

        when(repository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

        boolean result = userService.confirmEmail(token);

        assertTrue(result);
        assertTrue(user.isEmailConfirmed());
        assertNull(user.getConfirmationToken());
        assertNull(user.getTokenExpiry());

        verify(repository).save(user);
    }

    @Test
    @DisplayName("Подтверждение email: истек токен")
    void confirmEmail_ExpiredToken_ReturnsFalse() {
        String token = "expired-token";
        user.setConfirmationToken(token);
        user.setTokenExpiry(LocalDateTime.now().minusHours(1));

        when(repository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

        boolean result = userService.confirmEmail(token);

        assertFalse(result);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Подтверждение email: несуществующий токен")
    void confirmEmail_InvalidToken_ReturnsFalse() {
        when(repository.findByConfirmationToken("invalid")).thenReturn(Optional.empty());

        boolean result = userService.confirmEmail("invalid");

        assertFalse(result);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Проверка пароля: валидные данные")
    void validatePassword_ValidCredentials_ReturnsTrue() {
        user.setEmailConfirmed(true);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        boolean result = userService.validatePassword("test@example.com", "password123");

        assertTrue(result);
    }

    @Test
    @DisplayName("Проверка пароля: email не подтвержден")
    void validatePassword_EmailNotConfirmed_Throws() {
        user.setEmailConfirmed(false);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(EmailNotConfirmedException.class,
                () -> userService.validatePassword("test@example.com", "password123"));
    }

    @Test
    @DisplayName("Повторная отправка письма: успешно")
    void resendConfirmationEmail_Success() {
        user.setEmailConfirmed(false);
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.resendConfirmationEmail("test@example.com");

        verify(emailService).sendConfirmationEmail(eq("test@example.com"), anyString());
        verify(repository).save(user);
    }

    @Test
    @DisplayName("Повторная отправка письма: email уже подтвержден")
    void resendConfirmationEmail_AlreadyConfirmed_Throws() {
        user.setEmailConfirmed(true);
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class,
                () -> userService.resendConfirmationEmail("test@example.com"));

        verify(repository, never()).save(any());
        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Найти по email: найден")
    void findByEmail_Found_ReturnsDto() {
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Найти по email: не найден")
    void findByEmail_NotFound_Throws() {
        when(repository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findByEmail("none@example.com"));
    }
}
