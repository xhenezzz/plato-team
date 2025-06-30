package aidyn.kelbetov.service.impl;

import aidyn.kelbetov.dto.EmailChangeToken;
import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.entity.User;
import aidyn.kelbetov.exception.EmailAlreadyExistException;
import aidyn.kelbetov.exception.EmailNotConfirmedException;
import aidyn.kelbetov.exception.UserAlreadyExistsException;
import aidyn.kelbetov.exception.UserNotFoundException;
import aidyn.kelbetov.mapper.UserMapper;
import aidyn.kelbetov.repo.EmailChangeTokenRepository;
import aidyn.kelbetov.repo.UserRepository;
import aidyn.kelbetov.service.EmailService;
import aidyn.kelbetov.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository repository, EmailChangeTokenRepository emailChangeTokenRepository, UserMapper userMapper, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.emailChangeTokenRepository = emailChangeTokenRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    @Override
    public UserDto registerUser(RegisterDto dto) {
        // Проверяем, не зарегистрирован ли уже пользователь
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        User user = userMapper.fromRegisterDto(dto, passwordEncoder);

        String confirmationToken = generateConfirmationToken();
        user.setConfirmationToken(confirmationToken);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24)); // токен действует 24 часа

        // Сохраняем
        User saved = repository.save(user);

        // Отправляем email
        emailService.sendConfirmationEmail(saved.getEmail(), confirmationToken);

        return userMapper.toDto(saved);
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return userMapper.toDto(user);
    }

    @Override
    public boolean confirmEmail(String token) {
        Optional<User> userOpt = repository.findByConfirmationToken(token);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Проверяем, не истек ли токен
        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Подтверждаем email
        user.setEmailConfirmed(true);
        user.setConfirmationToken(null);
        user.setTokenExpiry(null);
        repository.save(user);

        return true;
    }

    @Override
    public boolean validatePassword(String email, String password) {
        Optional<User> userOpt = repository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Проверяем, подтвержден ли email
        if (!user.isEmailConfirmed()) {
            throw new EmailNotConfirmedException("Email не подтвержден");
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public void resendConfirmationEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (user.isEmailConfirmed()) {
            throw new IllegalStateException("Email уже подтвержден");
        }

        // Генерируем новый токен
        String newToken = generateConfirmationToken();
        user.setConfirmationToken(newToken);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        repository.save(user);

        // Отправляем email
        emailService.sendConfirmationEmail(user.getEmail(), newToken);
    }

    @Override
    public void requestEmailChange(String newEmail) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if(repository.existsByEmail(newEmail)){
            throw new EmailAlreadyExistException("Email уже используется другим пользователем!");
        }

        String token = generateConfirmationToken();

        EmailChangeToken changeToken = new EmailChangeToken();
        changeToken.setToken(token);
        changeToken.setNewEmail(newEmail);
        changeToken.setOldEmail(currentEmail);
        changeToken.setExpiry(LocalDateTime.now().plusHours(24));

        emailChangeTokenRepository.save(changeToken);
        emailService.sendEmailChangeConfirm(newEmail, token);
    }

    @Override
    public boolean confirmEmailChange(String token) {
        Optional<EmailChangeToken> opt = emailChangeTokenRepository.findByToken(token);
        if(opt.isEmpty()) return false;

        EmailChangeToken changeToken = opt.get();
        if(changeToken.getExpiry().isBefore(LocalDateTime.now())) return false;

        User user = repository.findByEmail(changeToken.getOldEmail())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));

        user.setEmail(changeToken.getNewEmail());
        repository.save(user);
        emailChangeTokenRepository.delete(changeToken);

        return true;
    }

    private String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }


}
