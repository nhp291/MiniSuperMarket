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

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    // Display the list of accounts
    @GetMapping("/AccountList")
    public String accountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        model.addAttribute("pageTitle", "Account List");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }


    @GetMapping("/AddAccount")
    public String addAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Add Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }


    @PostMapping("/AddAccount")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                             @RequestParam("imageUrl") MultipartFile imageFile, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Add Account");
            return "admin/layout";
        }

        if (imageFile.isEmpty()) {
            // Gán đường dẫn của hình ảnh mẫu mặc định nếu người dùng không tải hình ảnh
            account.setImageUrl("/Image/User.jpg");
        } else {

            // Lấy tên file của hình ảnh được tải lên
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

            // Thư mục để lưu hình ảnh
            String uploadDir = "Image/";
            Path uploadPath = Paths.get(uploadDir);

            // Nếu thư mục chưa tồn tại, tạo thư mục
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file ảnh vào thư mục chỉ định
            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Could not save image file: " + fileName, e);
            }

            // Gán đường dẫn hình ảnh vừa tải lên cho tài khoản
            account.setImageUrl("/Image/" + fileName);
        }
        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    // Display the form to edit an account
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
        System.out.println(roleRepository);
        return "admin/layout";
    }

    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                                @RequestParam("imageUrl") MultipartFile imageFile, @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword, Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Edit Account");
            return "admin/layout";
        }

        // Kiểm tra mật khẩu
        if (!password.equals("*****") && !password.isEmpty() && password.equals(confirmPassword)) {
            account.setPassword(password);
        } else {
            // Giữ mật khẩu cũ
            Account existingAccount = accountService.getAccountById(account.getAccountId());
            account.setPassword(existingAccount.getPassword());
        }

        // Xử lý hình ảnh
        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uploadDir = "Image/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Could not save image file: " + fileName, e);
            }
            account.setImageUrl("/Image/" + fileName);
        } else {
            // Giữ nguyên hình ảnh cũ
            Account existingAccount = accountService.getAccountById(account.getAccountId());
            if (existingAccount.getImageUrl() == null || existingAccount.getImageUrl().isEmpty()) {
                account.setImageUrl("/Image/User.png"); // Đặt hình ảnh mặc định
            } else {
                account.setImageUrl(existingAccount.getImageUrl()); // Giữ nguyên hình ảnh cũ
            }
        }

        // Lưu thông tin tài khoản đã cập nhật
        accountService.saveAccount(account);

        return "redirect:/Accounts/AccountList";
    }


    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList";
    }
}