package aidyn.kelbetov.service;

public interface EmailService {
    void sendConfirmationEmail(String email, String token);
    String buildEmailContent(String confirmationUrl);
    void sendEmailChangeConfirm(String newEmail, String token);
}
