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
    public String register(@ModelAttribute Account account, @ModelAttribute("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes) {
        if (!account.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu và xác nhận mật khẩu không khớp!");
            return "redirect:/register"; // Quay lại trang đăng ký với thông báo lỗi
        }

        try {
            accountService.saveAccountUser(account);  // Lưu tài khoản vào cơ sở dữ liệu
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:user/login"; // Chuyển hướng đến trang đăng nhập với thông báo thành công
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register"; // Quay lại trang đăng ký với thông báo lỗi
        }
    }
}



