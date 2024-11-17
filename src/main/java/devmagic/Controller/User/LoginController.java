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

    /**
     * Phương thức xử lý đăng nhập khi người dùng gửi yêu cầu đăng nhập qua biểu mẫu.
     * Kiểm tra thông tin tài khoản, phân quyền và chuyển hướng đến các trang tương ứng dựa trên vai trò của người dùng.
     *
     * @param username Tên đăng nhập người dùng (được truyền từ biểu mẫu đăng nhập).
     * @param password Mật khẩu người dùng (được truyền từ biểu mẫu đăng nhập).
     * @param model    Đối tượng model dùng để chuyển dữ liệu sang giao diện.
     * @return Chuỗi xác định trang tiếp theo (chuyển hướng).
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpSession session,
                        HttpServletResponse response) {

        if (userService.checkLogin(username, password)) {
            Account account = userService.findByUsername(username);

            if (account != null) {
                // Lưu thông tin vào session
                session.setAttribute("username", account.getUsername());
                session.setAttribute("role", account.getRole().getRoleName());
                session.setAttribute("user", account);

                // Tạo cookie để lưu thông tin
                Cookie usernameCookie = new Cookie("username", account.getUsername());
                Cookie roleCookie = new Cookie("role", account.getRole().getRoleName());
                roleCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
                usernameCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
                usernameCookie.setHttpOnly(true);
                roleCookie.setHttpOnly(true);
                response.addCookie(usernameCookie);
                response.addCookie(roleCookie);

                // Phân quyền
                if ("Admin".equals(account.getRole().getRoleName())) {
                    return "redirect:/Admin/Home";
                } else {
                    return "redirect:/layout/Home";
                }
            }
        }

        model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        return "user/login";
    }



}
