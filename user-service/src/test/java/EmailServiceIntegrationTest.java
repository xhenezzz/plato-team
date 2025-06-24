import aidyn.kelbetov.service.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

// Дополнительный тест для проверки с реальным SMTP сервером (для локального тестирования)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    @Order(1)
    @Disabled("Включить только для локального тестирования с реальным SMTP")
    void sendRealEmail_ManualTest() {
        // Для тестирования с реальным email сервером
        // Используйте Mailtrap, MailHog или подобный сервис

        String testEmail = "your-test-email@example.com";
        String testToken = "test-token-" + System.currentTimeMillis();

        assertDoesNotThrow(() -> {
            emailService.sendConfirmationEmail(testEmail, testToken);
        });

        System.out.println("Email отправлен на: " + testEmail);
        System.out.println("Проверьте почтовый ящик!");
    }
}