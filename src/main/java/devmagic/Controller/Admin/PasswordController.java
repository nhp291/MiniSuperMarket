package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Service .AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Password")
public class PasswordController {

    @Autowired
    private AccountService accountService;

    // Hiển thị form thay đổi mật khẩu
    @GetMapping("/ChangePassword")
    public String changePassword(@RequestParam("username") String username, Model model) {
        System.out.println("Username nhận được: " + username);
        model.addAttribute("username", username);
        return "UpdatePassword"; // Đảm bảo tên file chính xác
    }

    @PostMapping("/ChangePassword")
    public String handleChangePassword(
            @RequestParam("username") String username,
            @RequestParam("password") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPasswordConfirm") String newPasswordConfirm,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Kiểm tra mật khẩu xác nhận
        if (!newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không trùng khớp.");
            return "UpdatePassword"; // Trả về trang cập nhật nếu có lỗi
        }

        // Tìm tài khoản người dùng theo username/email
        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
            return "UpdatePassword"; // Trả về trang cập nhật nếu có lỗi
        }

        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!account.getPassword().equals(currentPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
            return "UpdatePassword"; // Trả về trang cập nhật nếu có lỗi
        }

        // Cập nhật mật khẩu mới
        account.setPassword(newPassword);
        accountService.saveAccount(account);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Mật khẩu đã thay đổi thành công.");

        // Chuyển hướng sang trang đăng nhập
        return "redirect:/user/login"; // Redirect tới form login
    }


    @GetMapping("/ForgotPassword")
    public String forgotPassword(@RequestParam("username") String username, Model model) {
        Account account = accountService.getAccountByUsername(username);
        if (account != null) {
            // Gửi số điện thoại và email về view
            model.addAttribute("contactInfo", account.getPhoneNumber());
            model.addAttribute("email", account.getEmail());
        } else {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
        }
        model.addAttribute("username", username);
        return "ForgotPassword";
    }


    // Xử lý thay đổi mật khẩu
//    @PostMapping("/ForgotPassword")
//    public String handleForgotPassword(@RequestParam("username") String username,
//                                       @RequestParam("newPassword") String newPassword,
//                                       RedirectAttributes redirectAttributes,
//                                       Model model) {
//
//        Account account = accountService.getAccountByUsername(username);
//        if (account == null) {
//            model.addAttribute("errorMessage", "Không tìm thấy tài khoản.");
//            return "ForgotPassword"; // Nếu không tìm thấy tài khoản, trả về form với lỗi
//        }
//
//        // Cập nhật mật khẩu mới
//        account.setPassword(newPassword);
//        accountService.saveAccount(account); // Cập nhật mật khẩu mới trong database
//
//        redirectAttributes.addFlashAttribute("successMessage", "Mật khẩu đã được thay đổi thành công.");
//        return "redirect:/Password/ForgotPassword?username=" + username; // Quay lại trang ForgotPassword với thông báo thành công
//    }

}
