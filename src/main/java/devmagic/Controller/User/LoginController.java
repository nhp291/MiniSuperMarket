package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private AccountService accountService;

    @RequestMapping("/user/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        return "user/login";
    }

    // Thêm phương thức này để xử lý đăng nhập thành công
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpSession session, HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
            return "user/login";
        }

        // Lấy tài khoản từ dịch vụ
        Optional<Account> accountOpt = accountService.findByUsername(username);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getRole() == null) {
                model.addAttribute("error", "Tài khoản của bạn chưa được gán vai trò.");
                return "user/login";
            }

            // Lưu thông tin tài khoản vào session
            session.setAttribute("accountId", account.getAccountId());
            session.setAttribute("username", account.getUsername());
            session.setAttribute("role", account.getRole().getRoleName());

            // Tạo cookies cho username và accountId
            Cookie usernameCookie = new Cookie("username", account.getUsername());
            usernameCookie.setMaxAge(7 * 24 * 60 * 60); // Cookie tồn tại trong 7 ngày
            usernameCookie.setPath("/");
            response.addCookie(usernameCookie);

            Cookie accountIdCookie = new Cookie("accountId", String.valueOf(account.getAccountId()));
            accountIdCookie.setMaxAge(7 * 24 * 60 * 60);
            accountIdCookie.setPath("/");
            response.addCookie(accountIdCookie);

            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");

            System.out.println("Role: " + account.getRole().getRoleName()); // Kiểm tra vai trò

            // Kiểm tra vai trò và điều hướng tới trang Admin hoặc User
            if ("Admin".equals(account.getRole().getRoleName())) {
                return "redirect:/Admin/Home"; // Dẫn tới trang Admin
            } else {
                return "redirect:/layout/Home"; // Dẫn tới trang Home của người dùng
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "user/login";
        }
    }

}
