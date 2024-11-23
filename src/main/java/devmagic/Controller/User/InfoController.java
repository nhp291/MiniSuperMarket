package devmagic.Controller.User;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/user")
public class InfoController {

    @Autowired
    private AccountService accountService;


    // Hiển thị thông tin người dùng
    @GetMapping("/info")
    public String getInfo(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("user");

        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("pageTitle", "My Profile");
            model.addAttribute("viewName", "admin/menu/MyProfile");
        } else {
            model.addAttribute("error", "Chưa đăng nhập");
            return "user/login";
        }
        return "user/info"; // Trả về template info.html
    }

    // Xử lý cập nhật thông tin
    // Xử lý cập nhật thông tin
    @PostMapping("/updateInfo")
    public String updateInfo(@ModelAttribute("account") Account account,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             HttpSession session,
                             Model model) throws IOException {
        if (result.hasErrors()) {
            return "user/info";
        }

        Account currentAccount = (Account) session.getAttribute("user");
        if (currentAccount == null) {
            model.addAttribute("error", "Bạn cần đăng nhập trước khi cập nhật thông tin.");
            return "user/login";
        }

        // Cập nhật thông tin người dùng
        currentAccount.setUsername(account.getUsername());
        currentAccount.setEmail(account.getEmail());
        currentAccount.setPhoneNumber(account.getPhoneNumber());
        currentAccount.setAddress(account.getAddress());

        // Xử lý ảnh đại diện nếu có
        if (!imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "src/main/resources/static/Image/imageProfile/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Kiểm tra và không thay thế ảnh nếu hình ảnh với tên tương tự đã tồn tại
                Path existingFilePath = uploadPath.resolve(fileName);
                if (Files.exists(existingFilePath)) {
                    // Nếu ảnh đã tồn tại, không thay thế, chỉ cập nhật tên ảnh
                    currentAccount.setImageUrl(fileName); // Chỉ cập nhật đường dẫn ảnh mà không thay thế file
                } else {
                    // Nếu ảnh chưa tồn tại, lưu ảnh mới
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        currentAccount.setImageUrl(fileName); // Cập nhật đường dẫn hình ảnh mới
                    } catch (IOException e) {
                        throw new IOException("Không thể lưu hình ảnh: " + fileName, e);
                    }
                }
            } else {
                throw new IOException("Tệp không phải là hình ảnh hợp lệ");
            }
        }

        // Lưu thông tin tài khoản đã thay đổi
        accountService.saveAccount(currentAccount);

        // Cập nhật thông tin trong session
        session.setAttribute("user", currentAccount);

        model.addAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/user/info"; // Điều hướng sau khi cập nhật thành công
    }

}
