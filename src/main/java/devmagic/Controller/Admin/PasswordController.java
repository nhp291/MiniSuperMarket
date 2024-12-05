package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Service .AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/Password")
public class PasswordController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị form thay đổi mật khẩu
    @GetMapping("/ChangePassword")
    public String changePassword(HttpSession session, Model model) {
        // Lấy thông tin tài khoản từ session
        Account account = (Account) session.getAttribute("Admin");
        if (account == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để thay đổi mật khẩu.");
            return "redirect:/user/login";
        }

        model.addAttribute("username", account.getUsername());
        return "UpdatePassword"; // Đảm bảo template phù hợp
    }


    @PostMapping("/ChangePassword")
    public String handleChangePassword(
            @RequestParam("username") String username,
            @RequestParam("password") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPasswordConfirm") String newPasswordConfirm,
            RedirectAttributes redirectAttributes,
            Model model) {

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
                model.addAttribute("errorMessage", "Bạn cần đăng nhập hoặc cung cấp tên đăng nhập.");
                return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập nếu không có session
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

}
