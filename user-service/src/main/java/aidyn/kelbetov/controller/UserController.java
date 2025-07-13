package aidyn.kelbetov.controller;

import aidyn.kelbetov.dto.EmailChangeRequest;
import aidyn.kelbetov.dto.RegisterDto;
import aidyn.kelbetov.dto.UserDto;
import aidyn.kelbetov.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    @Value("${internal.secret}")
    private String internalSecret;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto dto) {
        UserDto userDto = userService.registerUser(dto);
        return ResponseEntity.status(201).body(userDto);
    }


    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        boolean confirmed = userService.confirmEmail(token);

        if (confirmed) {
            return ResponseEntity.ok("Email успешно подтвержден!");
        } else {
            return ResponseEntity.badRequest().body("Неверный или истекший токен");
        }
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resendConfirmation(@RequestParam String email) {
        userService.resendConfirmationEmail(email);
        return ResponseEntity.ok("Письмо с подтверждением отправлено повторно");
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email,
                                                  @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key) {
        if(key == null || !key.equals(internalSecret)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDto userDto = userService.findByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/change-email")
    public ResponseEntity<String> requestEmailChange(@RequestHeader("X-User-Email") String currentEmail,
                                                     @RequestBody EmailChangeRequest request) {
        System.out.println("📩 Получен X-User-Email: " + currentEmail); // ← Добавь
        userService.requestEmailChange(currentEmail, request.getNewEmail());
        return ResponseEntity.ok("Письмо с подтверждением отправлено");
    }
}
