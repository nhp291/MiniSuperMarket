package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Service.AccountService;
import devmagic.Reponsitory.RoleRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Accounts")
public class AccountController {

    private final AccountService accountService;
    private final RoleRepository roleRepository;

    // Constructor Dependency Injection
    @Autowired
    public AccountController(AccountService accountService, RoleRepository roleRepository) {
        this.accountService = accountService;
        this.roleRepository = roleRepository;
    }

    // Hiển thị danh sách tài khoản
    @GetMapping("/AccountList")
    public String accountList(Model model) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("pageTitle", "Account List");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    // Form để thêm tài khoản
    @GetMapping("/AddAccount")
    public String addAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Add Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    // Xử lý khi người dùng submit form thêm tài khoản
    @PostMapping("/AddAccount")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                             @RequestParam("imageUrl") MultipartFile imageFile, Model model) throws IOException {
        // Kiểm tra lỗi xác thực dữ liệu
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Add Account");
            model.addAttribute("viewName", "admin/menu/AddAccount");
            return "admin/layout";
        }

        // Xử lý hình ảnh khi tải lên
        account.setImageUrl(saveImage(imageFile, "/Image/User.jpg"));
        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    // Hiển thị form chỉnh sửa tài khoản
    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable("id") Integer id, Model model) {
        Account account = accountService.getAccountById(id);
        if (account == null) {
            model.addAttribute("error", "Account not found!");
            return "redirect:/Accounts/AccountList";
        }
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Edit Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    // Xử lý khi người dùng submit form chỉnh sửa tài khoản
    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                                @RequestParam("imageUrl") MultipartFile imageFile, @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Edit Account");
            model.addAttribute("viewName", "admin/menu/AddAccount");
            return "admin/layout";
        }

        // Lấy tài khoản cũ để so sánh
        Account existingAccount = accountService.getAccountById(account.getAccountId());

        // Kiểm tra mật khẩu, chỉ cập nhật nếu mật khẩu mới và xác nhận giống nhau
        if (!password.equals("*****") && !password.isEmpty() && password.equals(confirmPassword)) {
            account.setPassword(password);
        } else {
            account.setPassword(existingAccount.getPassword());
        }

        // Lưu đường dẫn ảnh nếu có thay đổi
        account.setImageUrl(saveImage(imageFile, existingAccount.getImageUrl()));
        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    // Xóa tài khoản
    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList";
    }

    // Phương thức tiện ích để lưu hình ảnh
    private String saveImage(MultipartFile imageFile, String defaultImageUrl) throws IOException {
        // Nếu không có ảnh tải lên, sử dụng ảnh mặc định
        if (imageFile.isEmpty()) {
            return defaultImageUrl;
        }

        // Làm sạch tên file để tránh ký tự đặc biệt
        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

        // Định nghĩa thư mục tải lên và kiểm tra nếu chưa tồn tại
        Path uploadPath = Paths.get("Image/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lưu file ảnh vào thư mục chỉ định
        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/Image/" + fileName; // Đường dẫn ảnh được lưu
    }
}
