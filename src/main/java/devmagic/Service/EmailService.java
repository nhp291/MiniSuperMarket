package devmagic.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Phương thức gửi email với subject và body
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true để gửi nội dung HTML

        mailSender.send(message);
    }

    // Phương thức gửi email xác nhận đơn hàng
    public void sendOrderConfirmationEmail(String to, String body) throws MessagingException {
        String subject = "Xác nhận đơn hàng của bạn tại DevMagic";
        sendEmail(to, subject, body);
    }
}

