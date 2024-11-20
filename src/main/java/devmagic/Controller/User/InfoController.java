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
    private UserService userService;

    // Hiển thị thông tin người dùng
    @GetMapping
    public String getInfo(Model model, HttpServletRequest request) {
        String username = getUsernameFromCookies(request);

        if (username == null) {
            model.addAttribute("error", "Bạn chưa đăng nhập. Vui lòng đăng nhập trước.");
            return "redirect:/login"; // Chuyển hướng đến trang đăng nhập
        }

        // Lấy thông tin người dùng từ database
        Account account = userService.findByUsername(username);
        if (account == null) {
            model.addAttribute("error", "Không tìm thấy thông tin tài khoản.");
            return "error"; // Trả về trang lỗi nếu không tìm thấy tài khoản
        }

        // Truyền thông tin người dùng vào view
        model.addAttribute("user", account);
        return "info"; // Trả về template info.html
    }

    // Xử lý cập nhật thông tin
    @PostMapping("/updateInfo")
    public String updateInfo(@ModelAttribute("user") Account updatedAccount, HttpServletRequest request, Model model) {
        String username = getUsernameFromCookies(request);

        if (username == null) {
            model.addAttribute("error", "Bạn chưa đăng nhập. Vui lòng đăng nhập trước.");
            return "redirect:/login";
        }

        Account existingAccount = userService.findByUsername(username);
        if (existingAccount == null) {
            model.addAttribute("error", "Không tìm thấy thông tin tài khoản.");
            return "error";
        }

        // Cập nhật thông tin người dùng
        existingAccount.setEmail(updatedAccount.getEmail());
        existingAccount.setPhoneNumber(updatedAccount.getPhoneNumber());
        existingAccount.setAddress(updatedAccount.getAddress());


        // Lưu lại thông tin
        userService.save(existingAccount);
        model.addAttribute("success", "Cập nhật thông tin thành công.");

        // Sau khi cập nhật, trả lại trang thông tin người dùng với các thay đổi đã lưu
        model.addAttribute("user", existingAccount);
        return "info"; // Trả về template info.html với thông tin đã cập nhật
    }

    // Phương thức lấy username từ cookie
    private String getUsernameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("username".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
