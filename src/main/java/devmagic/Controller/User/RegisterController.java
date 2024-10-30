package devmagic.Controller.User;

import com.google.zxing.WriterException;
import devmagic.Model.Account;
import devmagic.Service.EmailService;
import devmagic.Service.QrCodeService;
import devmagic.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Base64;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "user/register";  // Tên trang giao diện đăng ký
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("phoneNumber") String phoneNumber,
                               @RequestParam("address") String address,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) throws WriterException, IOException {
        // Kiểm tra nếu mật khẩu và xác nhận mật khẩu không khớp
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            return "user/register";
        }

        // Tạo tài khoản mới
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setEmail(email);
        newAccount.setPhoneNumber(phoneNumber);
        newAccount.setAddress(address);
        newAccount.setPassword(password);

        // Kiểm tra nếu tên đăng nhập hoặc email đã tồn tại
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "user/register";
        }
        if (userService.findByEmail(email) != null) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "user/register";
        }

        // Lưu tài khoản vào cơ sở dữ liệu
        userService.createAccount(newAccount);

        // Nếu đăng ký thành công, tạo mã QR cho trang chủ và thông báo
        String qrText = "Chúc mừng! Bạn đã đăng ký thành công tại DevMagic.";
        QrCodeService qrCodeService = new QrCodeService();
        byte[] qrImage = qrCodeService.generateQRCodeImage(qrText, 250, 250);
        String base64QRImage = Base64.getEncoder().encodeToString(qrImage);
        String qrImageUrl = "data:image/png;base64," + base64QRImage;  // Định dạng base64 để hiển thị

        // Gửi email chứa mã QR
        EmailService emailService = new EmailService();
        emailService.sendEmail(email, qrImageUrl);

        // Gửi mã QR tới view
        model.addAttribute("qrImage", qrImageUrl);
        return "registration-success";  // Chuyển hướng đến trang hiển thị
    }

}
