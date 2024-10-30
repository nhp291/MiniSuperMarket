package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import devmagic.Service.UserService;
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
                        Model model) {
        // Kiểm tra thông tin đăng nhập (tên đăng nhập và mật khẩu)
        if (userService.checkLogin(username, password)) {
            // Lấy thông tin tài khoản dựa trên tên đăng nhập
            Account account = userService.findByUsername(username);

            // Kiểm tra tài khoản người dùng có tồn tại không
            if (account != null) {
                // Kiểm tra quyền dựa trên vai trò của tài khoản
                if ("Admin".equals(account.getRole())) {
                    // Nếu người dùng là admin, chuyển hướng tới trang quản trị
                    return "redirect:/Admin/Home";
                } else {
                    // Nếu người dùng là tài khoản thông thường, chuyển hướng tới trang sản phẩm
                    return "redirect:/layout/Product";
                }
            }
        } else {
            // Nếu thông tin đăng nhập không chính xác, hiển thị lại trang đăng nhập với thông báo lỗi
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            return "user/login";
        }

        // Trường hợp bất ngờ (không xác định), quay lại trang đăng nhập
        return "user/login";
    }
}
