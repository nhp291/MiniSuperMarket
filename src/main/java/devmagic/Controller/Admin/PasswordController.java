package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Service .AccountService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Optional;

@Controller
@RequestMapping("/Password")
public class PasswordController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender emailSender;

    // Hiển thị form thay đổi mật khẩu
    @GetMapping("/ChangePassword")
    public String changePassword(HttpSession session, Model model) {
        // Check if there is an authenticated user in the session
        Account account = (Account) session.getAttribute("Admin");
        if (account == null) {
            account = (Account) session.getAttribute("User");
        }

        if (account == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để thay đổi mật khẩu.");
            return "redirect:/user/login";
        }

        model.addAttribute("username", account.getUsername());
        return "UpdatePassword";
    }

    @PostMapping("/ChangePassword")
    public String handleChangePassword(
            @RequestParam("username") String username,
            @RequestParam("password") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPasswordConfirm") String newPasswordConfirm,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) { // Thêm HttpSession vào tham số

        // Xác nhận mật khẩu mới và mật khẩu xác nhận
        if (!newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không trùng khớp.");
            model.addAttribute("username", username); // Truyền lại username để hiển thị
            return "UpdatePassword";
        }

        // Tìm tài khoản người dùng
        Optional<Account> accountOptional = accountService.getAccountByUsername(username);
        if (accountOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
            model.addAttribute("username", username); // Truyền lại username để hiển thị
            return "UpdatePassword";
        }

        Account account = accountOptional.get();

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            model.addAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
            model.addAttribute("username", username); // Truyền lại username để hiển thị
            return "UpdatePassword";
        }

        // Mã hóa mật khẩu mới và cập nhật
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);
        accountService.saveAccount(account);

        // Cập nhật lại thông tin tài khoản vào session
        if (session.getAttribute("Admin") != null) {
            session.setAttribute("Admin", account); // Cập nhật thông tin Admin trong session
        } else if (session.getAttribute("User") != null) {
            session.setAttribute("User", account); // Cập nhật thông tin User trong session
        }

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Mật khẩu đã thay đổi thành công.");

        // Chuyển hướng đến trang đăng nhập
        return "redirect:/user/login";
    }

    @GetMapping("/ForgotPassword")
    public String forgotPassword(
            @RequestParam(value = "username", required = false) String username,
            HttpSession session,
            Model model) {

        // Nếu username không được cung cấp, lấy từ session
        if (username == null || username.isEmpty()) {
            Account account = (Account) session.getAttribute("Admin");
            if (account == null) {
                account = (Account) session.getAttribute("User");
            }
            username = account.getUsername(); // Lấy username từ tài khoản trong session
        }

        // Tìm tài khoản dựa trên username
        Optional<Account> accountOptional = accountService.getAccountByUsername(username);
        if (accountOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
            return "ForgotPassword";
        }

        Account account = accountOptional.get();
        model.addAttribute("contactInfo", account.getPhoneNumber());
        model.addAttribute("email", account.getEmail());
        model.addAttribute("username", username);

        return "ForgotPassword";
    }
    // Xử lý khi người dùng gửi form quên mật khẩu
    @PostMapping("/ForgotPassword")
    public String forgotPassword(
            @RequestParam("email") String email,
            Model model,
            HttpSession session) { // Thêm HttpSession vào tham số

        // Tìm tài khoản dựa trên email
        Optional<Account> accountOptional = accountService.getAccountByEmail(email);
        if (accountOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
            return "ForgotPassword";
        }

        Account account = accountOptional.get();

        // Tạo mật khẩu mới ngẫu nhiên
        String newPassword = generateRandomPassword(6);

        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);
        accountService.saveAccount(account); // Lưu mật khẩu mới vào cơ sở dữ liệu

        // Cập nhật lại thông tin tài khoản vào session
        if (session.getAttribute("Admin") != null) {
            session.setAttribute("Admin", account); // Cập nhật thông tin Admin trong session
        } else if (session.getAttribute("User") != null) {
            session.setAttribute("User", account); // Cập nhật thông tin User trong session
        }

        // Gửi email thông báo mật khẩu mới
        try {
            sendEmail(account.getEmail(), newPassword);
        } catch (MessagingException e) {
            model.addAttribute("errorMessage", "Không thể gửi email. Vui lòng thử lại.");
            return "ForgotPassword";
        }

        // Thêm thông báo thành công và chuyển hướng đến trang đăng nhập
        model.addAttribute("successMessage", "Mật khẩu mới đã được gửi qua email. Vui lòng đăng nhập lại.");
        return "redirect:/user/login";
    }

    // Tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    // Gửi email thông báo mật khẩu mới
    private void sendEmail(String to, String newPassword) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Mật khẩu mới của bạn");
        helper.setText("Mật khẩu mới của bạn là: " + newPassword);

        emailSender.send(message);
    }

}
