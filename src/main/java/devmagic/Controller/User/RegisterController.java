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
            // Kiểm tra dữ liệu từ form
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Tên đăng nhập không được để trống!");
                return "register"; // Trả về trang đăng ký
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

            // Kiểm tra định dạng email
            String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
            if (!account.getEmail().matches(emailPattern)) {
                model.addAttribute("errorMessage", "Email không đúng định dạng!");
                return "register";
            }

            // Kiểm tra định dạng số điện thoại (nếu có)
            if (account.getPhoneNumber() != null && !account.getPhoneNumber().isEmpty()) {
                String phonePattern = "^(0|\\+84)(\\d{9})$";
                if (!account.getPhoneNumber().matches(phonePattern)) {
                    model.addAttribute("errorMessage", "Số điện thoại không hợp lệ! Bắt đầu bằng '0' hoặc '+84' và có đúng 9 chữ số.");
                    return "register";
                }
            }

            // Gọi service để lưu tài khoản
            accountService.saveAccountUser(account, confirmPassword);

            // Thông báo thành công
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "login"; // Chuyển hướng đến trang đăng nhập
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi từ tầng Service và hiển thị lỗi
            model.addAttribute("errorMessage", e.getMessage());
            return "register"; // Quay lại trang đăng ký
        } catch (Exception e) {
            // Xử lý các lỗi không mong muốn và hiển thị lỗi chi tiết
            model.addAttribute("errorMessage", "Lỗi không mong muốn: " + e.getMessage());
            return "register";
        }
    }
}
