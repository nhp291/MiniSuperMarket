package devmagic.Controller.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


public class CustomHttpServletReques {
    @GetMapping("/profile")
    public String userProfile(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies(); // Lấy danh sách cookie
        String username = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue(); // Lấy giá trị cookie
                    break;
                }
            }
        }

        model.addAttribute("username", username);
        return "user/login";
    }


}
