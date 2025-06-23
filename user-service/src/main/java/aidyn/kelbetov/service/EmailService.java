package aidyn.kelbetov.service;

public interface EmailService {
    public void sendConfirmationEmail(String email, String token);
    public String buildEmailContent(String confirmationUrl);
}
