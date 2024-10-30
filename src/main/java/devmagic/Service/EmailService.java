package devmagic.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;



public class EmailService {

    private final String fromEmail = "Phatanhvu147963kk@gmail.com"; // Địa chỉ email gửi
    private final String password = "147963kk"; // Mật khẩu email gửi

    public void sendEmail(String toEmail, String qrImage) {
        // Thiết lập thuộc tính cho phiên làm việc
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Tạo một phiên làm việc
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {
            // Tạo một đối tượng MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã QR Đăng Ký Thành Công");
            message.setText("""
                    Chúc mừng! Bạn đã đăng ký thành công tại DevMagic.

                    Dưới đây là mã QR của bạn:""");

            // Gửi mã QR dưới dạng nội dung HTML
            String emailContent = "<p>Chúc mừng! Bạn đã đăng ký thành công tại DevMagic.</p>" +
                    "<p>Dưới đây là mã QR của bạn:</p>" +
                    "<img src='" + qrImage + "' alt='QR Code' />";
            message.setContent(emailContent, "text/html");

            // Gửi email
            Transport.send(message);
            System.out.println("Email đã được gửi thành công!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
