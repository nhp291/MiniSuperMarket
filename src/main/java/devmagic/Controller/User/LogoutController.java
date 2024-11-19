package devmagic.Controller.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        // Xóa session
        session.invalidate();

        // Xóa cookie bằng cách đặt thời gian sống về 0
        deleteCookie("username", response);
        deleteCookie("accountId", response);

        // Chuyển hướng về trang đăng nhập mà không có tham số trên URL
        return "redirect:/user/login";
    }

    private void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
