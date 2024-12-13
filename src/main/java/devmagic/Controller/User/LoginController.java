package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
//import devmagic.Service.TidioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    // Hiển thị trang đăng nhập
    @RequestMapping("/user/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "success", required = false) String success,
                            Model model) {
        // Truyền thông báo lỗi hoặc thành công (nếu có)
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "user/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpSession session, HttpServletResponse response,
                        HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                model.addAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
                return "user/login"; // Trả về trang đăng nhập với thông báo lỗi
            }

            Optional<Account> accountOpt = accountService.findByUsername(username);

            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();

                if (account.getRole() == null) {
                    model.addAttribute("error", "Tài khoản của bạn chưa được gán vai trò.");
                    return "user/login"; // Trả về trang đăng nhập với thông báo lỗi
                }

                if (!account.getPassword().equals(password)) {
                    model.addAttribute("error", "Mật khẩu không chính xác!");
                    return "user/login"; // Trả về trang đăng nhập với thông báo lỗi
                }
                session.setAttribute("accountId", account.getAccountId());
                session.setAttribute("username", account.getUsername());
                session.setAttribute("role", account.getRole().getRoleName());

                Cookie usernameCookie = new Cookie("username", account.getUsername());
                usernameCookie.setMaxAge(7 * 24 * 60 * 60);
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);

                Cookie accountIdCookie = new Cookie("accountId", String.valueOf(account.getAccountId()));
                accountIdCookie.setMaxAge(7 * 24 * 60 * 60);
                accountIdCookie.setPath("/");
                response.addCookie(accountIdCookie);

                // Lấy địa chỉ IP của người dùng
                String ipAddress = request.getRemoteAddr();
                account.setIp(ipAddress);


                redirectAttributes.addAttribute("success", "Đăng nhập thành công!");

                if ("Admin".equals(account.getRole().getRoleName())) {
                    return "redirect:/Admin/Home";
                } else {
                    return "redirect:/layout/Home";
                }
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                return "user/login"; // Trả về trang đăng nhập với thông báo lỗi
            }
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình đăng nhập. Vui lòng thử lại sau!");
            e.printStackTrace();
            return "user/login"; // Trả về trang đăng nhập với thông báo lỗi
        }
    }
}