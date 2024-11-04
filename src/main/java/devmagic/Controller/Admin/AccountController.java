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
import java.util.List;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    private static final Logger LOGGER = Logger.getLogger(AccountController.class.getName());
    private static final String UPLOAD_DIR = "Image/imageProfile/";

    // Phương thức xử lý lưu tài khoản và quản lý tải lên hình ảnh
    private void handleAccountSave(Account account, MultipartFile imageFile, String password,
                                   String confirmPassword) throws IOException {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu không trùng khớp!");
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                account.setImageUrl("/" + UPLOAD_DIR + fileName);
            }
        } else if (account.getImageUrl() == null || account.getImageUrl().isEmpty()) {
            account.setImageUrl("/" + UPLOAD_DIR + "User.jpg");
        }

        accountService.saveAccount(account);
    }

    // Lấy danh sách tài khoản
    @GetMapping("/AccountList")
    public String accountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        model.addAttribute("pageTitle", "Danh sách tài khoản");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    // Mở form thêm tài khoản
    @GetMapping("/AddAccount")
    public String addAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Thêm tài khoản");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    // Xử lý thêm tài khoản mới
    @PostMapping("/AddAccount")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                             @RequestParam("imageUrl") MultipartFile imageFile,
                             @RequestParam("password") String password,
                             @RequestParam("confirmPassword") String confirmPassword,
                             Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Thêm tài khoản");
            return "admin/layout";
        }

        try {
            handleAccountSave(account, imageFile, password, confirmPassword);
        } catch (IllegalArgumentException e) {
            result.rejectValue("password", "error.account", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/layout";
        }

        return "redirect:/Accounts/AccountList";
    }

    // Mở form chỉnh sửa tài khoản
    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable("id") Integer id, Model model) {
        Account account = accountService.getAccountById(id);
        if (account == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản!");
            return "redirect:/Accounts/AccountList";
        }
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Chỉnh sửa tài khoản");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    // Cập nhật tài khoản
    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                                @RequestParam("imageUrl") MultipartFile imageFile,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Chỉnh sửa tài khoản");
            return "admin/layout";
        }

        try {
            handleAccountSave(account, imageFile, password, confirmPassword);
        } catch (IllegalArgumentException e) {
            result.rejectValue("password", "error.account", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/layout";
        }

        return "redirect:/Accounts/AccountList";
    }

    // Xóa tài khoản
    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList";
    }
}