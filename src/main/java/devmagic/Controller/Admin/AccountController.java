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
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/AccountList")
    public String accountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts()
                .stream()
                .filter(Objects::nonNull) // Loại bỏ các tài khoản null
                .collect(Collectors.toList());

        for (Account account : accounts) {
            if (account == null) {
                System.out.println("Found null account in the list!");
            } else {
                System.out.println("Account: " + account);
            }

            if (account.getImageUrl() == null) {
                account.setImageUrl("/Image/imageProfile/User.png"); // Default image
            }
        }

        model.addAttribute("accounts", accounts);
        model.addAttribute("pageTitle", "Account List");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    @GetMapping("/AddAccount")
    public String addAccountForm(Model model) {
        model.addAttribute("account", new Account());  // Khởi tạo một đối tượng Account mới
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Add Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    @PostMapping("/AddAccount")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Thêm tài khoản");
            model.addAttribute("viewName", "admin/menu/AddAccount");
            return "admin/layout";
        }

        // Đường dẫn trong project để lưu hình ảnh
        String projectImageDir = "src/main/resources/static/Image/imageProfile/";

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get(projectImageDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Không thể lưu tệp hình ảnh: " + fileName, e);
            }

            account.setImageUrl(fileName); // URL để truy cập hình ảnh
        } else {
            account.setImageUrl("/Image/imageProfile/User.jpg"); // Hình ảnh mặc định
        }

        // Lưu tài khoản
        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }


    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable("id") Integer id, Model model) {
        // Lấy account từ database
        Account account = accountService.getAccountById(id);
        if (account == null) {
            model.addAttribute("error", "Account not found!");
            return "redirect:/Accounts/AccountList";
        }
        // Truyền account vào model
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Edit Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
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

        if (!password.equals("*****") && !password.isEmpty() && password.equals(confirmPassword)) {
            account.setPassword(password);
        } else {
            Account existingAccount = accountService.getAccountById(account.getAccountId());
            account.setPassword(existingAccount.getPassword());
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uploadDir = "Image/imageProfile/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Không thể lưu hình ảnh: " + fileName, e);
            }
            account.setImageUrl( fileName);
        } else {
            Account existingAccount = accountService.getAccountById(account.getAccountId());
            if (existingAccount.getImageUrl() == null || existingAccount.getImageUrl().isEmpty()) {
                account.setImageUrl("/Image/imageProfile/User.png"); // Hình ảnh mặc định nếu không có
            } else {
                account.setImageUrl(existingAccount.getImageUrl()); // Giữ nguyên hình ảnh cũ
            }
        }

        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList";
    }
}