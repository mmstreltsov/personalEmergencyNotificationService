package ru.hse.mmstr_project.se.service.sender.implementations;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.hse.mmstr_project.se.kafka.dto.SenderRequestDto;

import java.util.Properties;

@Component
public class EmailSenderLogic implements CommonSenderLogic {

    private final String fromEmail;
    private final String password;
    private final Properties props;

    public EmailSenderLogic(
            @Value("${email.sender.smtp.email}") String fromEmail,
            @Value("${email.sender.smtp.password}") String password) {
        this.fromEmail = fromEmail;
        this.password = password;

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    @Override
    public boolean sendMessage(SenderRequestDto request) {
        try {
            return sendMessageImpl(request);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendMessageImpl(SenderRequestDto request) throws MessagingException {
        String destinationEmail = request.email();
        if (destinationEmail.isBlank() || destinationEmail.isEmpty()) {
            return true;
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinationEmail));
        message.setSubject("Emergency Notification");

        Multipart multipart = new MimeMultipart();

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(request.text());
        multipart.addBodyPart(textPart);

        if (request.data() != null && request.data().length > 0) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(request.data(), "image/jpeg");
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("Photo.jpg");
            multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);

        Transport.send(message);
        return true;
    }
}
