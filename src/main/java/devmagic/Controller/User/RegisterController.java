package devmagic.Controller.User;
import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("phoneNumber") String phoneNumber,
                               @RequestParam("address") String address,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) throws IOException {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp!");
            return "user/register";
        }

        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "user/register";
        }

        if (userService.findByEmail(email) != null) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "user/register";
        }

        // Mặc định gán roleId = 1 (User role)
        Role userRole = userService.findRoleById(1);  // Giả sử roleId = 1 là User

        // Tạo tài khoản mới
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setEmail(email);
        newAccount.setPhoneNumber(phoneNumber);
        newAccount.setAddress(address);
        newAccount.setPassword(password);
        newAccount.setRole(userRole);  // Gán vai trò vào tài khoản

        try {
            userService.createAccount(newAccount); // Tạo tài khoản
            model.addAttribute("success", "Tạo tài khoản thành công! Bạn có thể đăng nhập ngay.");
        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra, vui lòng thử lại.");
            return "user/register";
        }

        // Chuyển hướng đến trang đăng nhập
        return "redirect:/login";
    }
}

