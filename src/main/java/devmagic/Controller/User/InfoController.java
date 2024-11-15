//package devmagic.Controller.User;
//
//import devmagic.Model.Account;
//import devmagic.Service.UserService;
//import org.apache.catalina.User;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//public class InfoController {
//
//    private UserService userService;
//
//    @GetMapping("/info")
//    public String getUserInfo(Model model, @RequestParam("userId") Integer userId) {
//        // Giả sử bạn đã có UserService để xử lý dữ liệu người dùng từ database
//        User user = userService.findById(userId);
//        model.addAttribute("user", user);
//        return "info";
//    }
//
//    @PostMapping("/updateInfo")
//    public String updateUserInfo(@ModelAttribute Account account) {
//        // Cập nhật thông tin người dùng
//
//        return "redirect:/info?userId=" + account.getId(); // Redirect về trang thông tin sau khi cập nhật
//    }
//
//}
