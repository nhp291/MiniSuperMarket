package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public String register(@ModelAttribute Account account,
                           @ModelAttribute("confirmPassword") String confirmPassword,
                           Model model) {
        try {
            // Kiểm tra dữ liệu nhập
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Tên đăng nhập không được để trống!");
                return "register";
            }

            if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Mật khẩu không được để trống!");
                return "register";
            }

            if (!account.getPassword().equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu và xác nhận mật khẩu không khớp!");
                return "register";
            }

            if (account.getEmail() == null || account.getEmail().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Email không được để trống!");
                return "register";
            }

            // Kiểm tra email hợp lệ
            String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
            if (!account.getEmail().matches(emailPattern)) {
                model.addAttribute("errorMessage", "Email không đúng định dạng!");
                return "register";
            }

            // Kiểm tra username/email đã tồn tại
            if (accountService.isUsernameExist(account.getUsername())) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                return "register";
            }
            if (accountService.isEmailExist(account.getEmail())) {
                model.addAttribute("errorMessage", "Email đã tồn tại!");
                return "register";
            }

            // Gọi service để lưu account
            accountService.saveAccount(account);

            // Thông báo đăng ký thành công
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }

}
