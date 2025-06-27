import aidyn.kelbetov.UserServiceApplication;
import aidyn.kelbetov.service.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

// Дополнительный тест для проверки с реальным SMTP сервером (для локального тестирования)

@SpringBootTest(classes = UserServiceApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    @Order(1)
    @Disabled("Включить только для локального тестирования с реальным SMTP")
    void sendRealEmail_ManualTest() {

        String testEmail = "adi2005kel@gmail.com";
        String testToken = "test-token-" + System.currentTimeMillis();

        assertDoesNotThrow(() -> {
            emailService.sendConfirmationEmail(testEmail, testToken);
        });

        System.out.println("Email отправлен на: " + testEmail);
        System.out.println("Проверьте почтовый ящик!");
    }
}