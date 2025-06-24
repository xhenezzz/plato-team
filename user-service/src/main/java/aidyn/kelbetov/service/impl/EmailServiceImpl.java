package aidyn.kelbetov.service.impl;

import aidyn.kelbetov.exception.EmailSendException;
import aidyn.kelbetov.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    //выставил временно 8080 для теста
    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;

    @Value("${app.name:Ваше приложение}")
    private String appName;

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
            helper.setFrom("aidyn.kelbetov@yandex.ru");

            String confirmationUrl = frontendUrl + "/api/users/confirm-email?token=" + token;
            String emailContent = buildEmailContent(confirmationUrl);

            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e){
            throw new EmailSendException("Ошибка отправки email: " + e.getMessage());
        }
    }

    @Override
    public String buildEmailContent(String confirmationUrl) {
        // Экранируем URL для безопасности
        String safeUrl = HtmlUtils.htmlEscape(confirmationUrl);

        return """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Подтверждение регистрации</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f4f4f4; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .button { 
                        display: inline-block;
                        background-color: #4CAF50; 
                        color: white; 
                        padding: 12px 24px; 
                        text-decoration: none; 
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer { 
                        background-color: #f4f4f4; 
                        padding: 20px; 
                        text-align: center; 
                        font-size: 12px; 
                        color: #666; 
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Добро пожаловать в %s!</h1>
                    </div>
                    <div class="content">
                        <h2>Подтверждение регистрации</h2>
                        <p>Спасибо за регистрацию! Для завершения процесса регистрации необходимо подтвердить ваш email адрес.</p>
                        <p>Нажмите на кнопку ниже для подтверждения:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Подтвердить Email</a>
                        </p>
                        <p>Если кнопка не работает, скопируйте и вставьте следующую ссылку в адресную строку браузера:</p>
                        <p style="word-break: break-all; background-color: #f4f4f4; padding: 10px; border-radius: 3px;">
                            %s
                        </p>
                        <p><strong>Важно:</strong> Ссылка действительна в течение 24 часов.</p>
                    </div>
                    <div class="footer">
                        <p>Если вы не регистрировались на нашем сайте, просто проигнорируйте это письмо.</p>
                        <p>© %s. Все права защищены.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, safeUrl, safeUrl, appName);
    }

    private String buildConfirmationUrl(String token) {
        if (!frontendUrl.startsWith("http://") && !frontendUrl.startsWith("https://")) {
            System.out.println("Frontend URL не содержит протокол: {}" + frontendUrl);
        }

        String baseUrl = frontendUrl.endsWith("/")
                ? frontendUrl.substring(0, frontendUrl.length() - 1)
                : frontendUrl;

        return baseUrl + "/api/users/confirm-email?token=" + token;
    }

    /**
     * Валидация параметров email и token
     */
    private void validateEmailParameters(String email, String token) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token не может быть пустым");
        }

        // Базовая валидация email формата
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Неверный формат email");
        }
    }
}
