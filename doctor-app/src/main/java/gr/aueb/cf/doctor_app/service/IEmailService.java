package gr.aueb.cf.doctor_app.service;

public interface IEmailService {

    void sendActivationEmail(String toEmail, String activationToken);
}
