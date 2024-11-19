package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class RegisterController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public String register(@ModelAttribute Account account,
                           @ModelAttribute("confirmPassword") String confirmPassword,
                           RedirectAttributes redirectAttributes) {
        // Kiểm tra các trường bắt buộc
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập không được để trống!");
            return "redirect:/register";
        }
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không được để trống!");
            return "redirect:/register";
        }
        if (!account.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu và xác nhận mật khẩu không khớp!");
            return "redirect:/register";
        }
        if (account.getEmail() == null || account.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email không được để trống!");
            return "redirect:/register";
        }

        // Kiểm tra xem username hoặc email đã tồn tại trong cơ sở dữ liệu
        if (accountService.isUsernameOrEmailExist(account.getUsername(), account.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập hoặc email đã tồn tại!");
            return "redirect:/register";
        }

        try {
            accountService.saveAccountUser(account); // Lưu tài khoản vào cơ sở dữ liệu
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/user/login"; // Chuyển hướng đến trang đăng nhập
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register"; // Quay lại trang đăng ký với thông báo lỗi
        }
    }
}

