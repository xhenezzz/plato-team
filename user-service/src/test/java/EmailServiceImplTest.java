import aidyn.kelbetov.exception.EmailSendException;
import aidyn.kelbetov.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "appName", "Test App");

        // По умолчанию мок всегда возвращает MimeMessage, чтобы не было NPE
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendConfirmationEmail_Success() throws MessagingException {
        String email = "test@example.com";
        String token = "test-token-123";

        emailService.sendConfirmationEmail(email, token);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendConfirmationEmail_MessagingException_ThrowsEmailSendException() throws MessagingException {
        String email = "test@example.com";
        String token = "test-token-123";

        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(mimeMessage);

        EmailSendException exception = assertThrows(EmailSendException.class,
                () -> emailService.sendConfirmationEmail(email, token));

        assertTrue(exception.getMessage().contains("Не удалось отправить письмо подтверждения"));
    }

    @Test
    void sendConfirmationEmail_NullEmail_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendConfirmationEmail(null, "token"));
    }

    @Test
    void sendConfirmationEmail_EmptyEmail_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendConfirmationEmail("", "token"));
    }

    @Test
    void sendConfirmationEmail_NullToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendConfirmationEmail("test@example.com", null));
    }

    @Test
    void sendConfirmationEmail_EmptyToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendConfirmationEmail("test@example.com", ""));
    }

    @Test
    void sendConfirmationEmail_InvalidEmailFormat_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> emailService.sendConfirmationEmail("invalid-email", "token"));
    }

    @Test
    void buildEmailContent_ContainsRequiredElements() {
        String confirmationUrl = "http://localhost:3000/confirm-email?token=test-token";

        String content = emailService.buildEmailContent(confirmationUrl);

        assertNotNull(content);
        assertTrue(content.contains("Test App"));
        assertTrue(content.contains("Подтверждение регистрации"));
        assertTrue(content.contains("Подтвердить Email"));
        assertTrue(content.contains(confirmationUrl));
        assertTrue(content.contains("24 часа"));
        assertTrue(content.contains("<!DOCTYPE html"));
    }

    @Test
    void buildEmailContent_EscapesSpecialCharacters() {
        String maliciousUrl = "http://localhost:3000/confirm?token=<script>alert('xss')</script>";

        String content = emailService.buildEmailContent(maliciousUrl);

        assertFalse(content.contains("<script>"));
        assertTrue(content.contains("&lt;script&gt;"));
    }

    @Test
    void buildEmailContent_ResponsiveDesign() {
        String confirmationUrl = "http://localhost:3000/confirm-email?token=test-token";

        String content = emailService.buildEmailContent(confirmationUrl);

        assertTrue(content.contains("viewport"));
        assertTrue(content.contains("max-width: 600px"));
        assertTrue(content.contains("word-break: break-all"));
    }
}
