package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import devmagic.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpSession session, HttpServletResponse response) {

        // Kiểm tra đầu vào trống
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
            return "user/login";
        }

        // Kiểm tra đăng nhập
        if (userService.checkLogin(username, password)) {
            Account account = userService.findByUsername(username);

            if (account != null) {
              
                // Kiểm tra vai trò
                if (account.getRole() == null) {
                    model.addAttribute("error", "Tài khoản của bạn chưa được gán vai trò. Vui lòng liên hệ quản trị viên.");
                    return "user/login";
                }

                // Lưu thông tin vào session
                initializeUserSession(session, account);

                // Tạo cookie lưu thông tin đăng nhập
                Cookie usernameCookie = new Cookie("username", account.getUsername());
                usernameCookie.setMaxAge(7 * 24 * 60 * 60); // Cookie tồn tại trong 7 ngày
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);

                // Tạo thêm cookie cho accountId nếu cần
                Cookie accountIdCookie = new Cookie("accountId", String.valueOf(account.getAccountId()));
                accountIdCookie.setMaxAge(7 * 24 * 60 * 60); // Cookie tồn tại trong 7 ngày
                accountIdCookie.setPath("/");
                response.addCookie(accountIdCookie);

                // Chuyển hướng dựa trên vai trò người dùng
                return getRedirectUrlBasedOnRole(account);
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        return "user/login";
    }

    private void initializeUserSession(HttpSession session, Account account) {
        session.setAttribute("accountId", account.getAccountId());
        session.setAttribute("username", account.getUsername());
        session.setAttribute("user", account);
    }

    private String getRedirectUrlBasedOnRole(Account account) {
        if (account.getRole() != null && "Admin".equals(account.getRole().getRoleName())) {
            return "redirect:/Admin/Home";
        } else {
            return "redirect:/layout/Home";
        }
    }
}
