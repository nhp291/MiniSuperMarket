package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private UserService userService; // Service tương tác với database

    // Hiển thị thông tin người dùng
    @GetMapping
    public String getInfo(Model model, HttpServletRequest request) {
        // Lấy cookie từ request
        Cookie[] cookies = request.getCookies();
        String username = null;

        // Tìm cookie chứa username
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                    break;
                }
            }
        }

        if (username != null) {
            // Lấy thông tin người dùng từ database
            Account account = userService.findByUsername(username);
            if (account != null) {
                model.addAttribute("user", account);
                return "info"; // Trả về template info.html
            } else {
                model.addAttribute("error", "Không tìm thấy tài khoản người dùng.");
                return "error"; // Trả về trang lỗi nếu không tìm thấy tài khoản
            }
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng trong cookie.");
            return "error"; // Hoặc trang lỗi nếu không có username trong cookie
        }
    }

    // Xử lý cập nhật thông tin
    @PostMapping("/updateInfo")
    public String updateInfo(@ModelAttribute("user") Account updatedAccount, HttpServletRequest request, Model model) {
        // Lấy cookie từ request
        Cookie[] cookies = request.getCookies();
        String username = null;

        // Tìm cookie chứa username
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                    break;
                }
            }
        }

        if (username != null) {
            Account existingAccount = userService.findByUsername(username);
            if (existingAccount != null) {
                // Cập nhật thông tin người dùng
                existingAccount.setUsername(updatedAccount.getUsername());
                existingAccount.setEmail(updatedAccount.getEmail());
                existingAccount.setPhoneNumber(updatedAccount.getPhoneNumber());
                existingAccount.setAddress(updatedAccount.getAddress());

                userService.save(existingAccount); // Lưu lại thông tin đã cập nhật
                model.addAttribute("success", "Cập nhật thông tin thành công!");
                return "redirect:/info?success"; // Chuyển hướng lại trang info với thông báo thành công
            } else {
                model.addAttribute("error", "Không tìm thấy tài khoản người dùng để cập nhật.");
                return "error"; // Trả về trang lỗi nếu không tìm thấy tài khoản
            }
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng trong cookie.");
            return "error"; // Nếu không tìm thấy cookie username
        }
    }
}

