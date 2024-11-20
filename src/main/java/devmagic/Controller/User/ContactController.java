package devmagic.Controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    // Hiển thị form liên hệ
    @GetMapping("/contact-form")  // Đổi đường dẫn từ /contact thành /contact-form
    public String showContactForm() {
        return "user/contact"; // Trả về trang liên hệ
    }

    // Xử lý form liên hệ
    @PostMapping("/contact-form/submit")  // Đổi đường dẫn từ /contact/submit thành /contact-form/submit
    public String submitContactForm(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("message") String message,
            Model model) {

        try {
            if (email.isEmpty() || message.isEmpty()) {
                throw new IllegalArgumentException("Email và nội dung tin nhắn không được để trống.");
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("phatanhvu147963kk@gmail.com");
            mailMessage.setTo("phatanhvu147963kk@gmail.com");
            mailMessage.setSubject("Liên hệ từ " + firstName + " " + lastName);
            mailMessage.setText(
                    "Tên: " + firstName + " " + lastName + "\n" +
                            "Email: " + email + "\n" +
                            "Số điện thoại: " + (phone != null ? phone : "Không cung cấp") + "\n\n" +
                            "Tin nhắn:\n" + message
            );

            mailSender.send(mailMessage);
            model.addAttribute("successMessage", "Tin nhắn của bạn đã được gửi thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong khi gửi tin nhắn. Vui lòng thử lại sau.");
            e.printStackTrace();
        }

        return "user/contact"; // Trả về trang liên hệ sau khi gửi
    }
}



