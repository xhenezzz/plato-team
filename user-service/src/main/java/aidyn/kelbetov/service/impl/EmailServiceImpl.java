package aidyn.kelbetov.service.impl;

import aidyn.kelbetov.exception.EmailSendException;
import aidyn.kelbetov.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${app.frontend.url:http://localhost:3000}")

    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendConfirmationEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Подверждение регистрации!");

            String confirmationUrl = frontendUrl + "/confirm-email?token=" + token;
            String emailContent = buildEmailContent(confirmationUrl);

            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e){
            throw new EmailSendException("Ошибка отправки email: " + e.getMessage());
        }
    }

    @Override
    public String buildEmailContent(String confirmationUrl) {
        return """
            <html>
                <body>
                    <h2>Добро пожаловать!</h2>
                    <p>Спасибо за регистрацию. Для подтверждения вашего email перейдите по ссылке:</p>
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                        Подтвердить Email
                    </a>
                    <p>Ссылка действительна 24 часа.</p>
                </body>
            </html>
            """.formatted(confirmationUrl);
    }
}
