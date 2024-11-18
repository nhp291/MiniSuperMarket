package devmagic.Controller.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    // Lấy accountId từ cookie và trả về thông tin người dùng
    @GetMapping("/layout/Home")
    public String dashboard(HttpServletRequest request, Model model) {
        String username = getUsernameFromCookies(request);
        Integer accountId = getAccountIdFromCookies(request);

        // Kiểm tra và lưu thông tin vào Model
        if (username != null) {
            model.addAttribute("username", username);
        } else {
            model.addAttribute("error", "Bạn cần đăng nhập để truy cập trang này.");
        }

        if (accountId != null) {
            model.addAttribute("accountId", accountId);
        }

        return "layout/Home"; // Trả về trang dashboard
    }

    private String getUsernameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    return cookie.getValue(); // Lấy giá trị username từ cookie
                }
            }
        }
        return null;
    }

    private Integer getAccountIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {
                    try {
                        return Integer.parseInt(cookie.getValue()); // Chuyển giá trị accountId từ cookie thành Integer
                    } catch (NumberFormatException e) {
                        // Lỗi khi chuyển đổi, có thể log hoặc xử lý thêm
                    }
                }
            }
        }
        return null;
    }
}

