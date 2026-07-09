package gr.aueb.cf.doctor_app.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.activation.base-url}")
    private String activationBaseUrl;

    @Override
    public void sendActivationEmail(String toEmail, String activationToken) {

        String activationLink = activationBaseUrl + "?token=" + activationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Please activate your doctor account");
        message.setText("Welcome to Doctor App. \n\n"
        + "Please activate your doctor account within 48 hours by clicking the link below:\n"
        +activationLink + "\n\n" + "If you did not expect that email, please ignore it.");

        mailSender.send(message);
        log.info("Activation mail sent to={}" , toEmail);

    }
}
