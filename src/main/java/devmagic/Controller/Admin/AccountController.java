package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.AccountRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/AccountList")
    public String accountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        accounts.forEach(account -> {
            if (account.getImageUrl() == null) {
                account.setImageUrl("User.png");
            }
        });

        model.addAttribute("accounts", accounts);
        model.addAttribute("pageTitle", "Account List");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    @GetMapping("/AccountForm")
    public String addAccountForm(Model model) {
        Account account = new Account();
        account.setRole(new Role());
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Add Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    @PostMapping("/AddAccount")
    public String addAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, Model model) throws IOException {

        if (validateAccount(account, result)) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Add Account");
            model.addAttribute("viewName", "admin/menu/AddAccount");
            return "admin/layout";
        }

        handleImageUpload(account, imageFile, "User.png");

        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable("id") Integer id, Model model) {
        Optional<Account> optionalAccount = accountService.getAccountById(id);

        if (optionalAccount.isEmpty()) {
            model.addAttribute("errorMessage", "Tài khoản không tồn tại.");
            return "redirect:/Accounts/AccountList";
        }

        model.addAttribute("account", optionalAccount.get());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Edit Account");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }

    @PostMapping("/update")
    public String updateAccount(@Valid @ModelAttribute("account") Account account, BindingResult result,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("pageTitle", "Edit Account");
            model.addAttribute("viewName", "admin/menu/AddAccount");
            return "admin/layout";
        }

        Optional<Account> existingAccountOptional = accountService.getAccountById(account.getAccountId());
        if (existingAccountOptional.isEmpty()) {
            result.rejectValue("accountId", "error.account", "Tài khoản không tồn tại.");
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/layout";
        }

        Account existingAccount = existingAccountOptional.get();
        account.setPassword(existingAccount.getPassword());
        handleImageUpload(account, imageFile, existingAccount.getImageUrl());
        validateAndUpdateEmail(account, existingAccount, result, model);

        account.setRole(Optional.ofNullable(account.getRole()).orElse(existingAccount.getRole()));
        accountService.saveAccount(account);
        return "redirect:/Accounts/AccountList";
    }

    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList";
    }

    private boolean validateAccount(Account account, BindingResult result) {
        boolean hasErrors = false;

        if (accountService.isUsernameExist(account.getUsername())) {
            result.rejectValue("username", "error.account", "Username đã tồn tại.");
            hasErrors = true;
        } else if (!account.getUsername().matches("^[a-zA-Z0-9._-]{3,}$")) {
            result.rejectValue("username", "error.account", "Username phải chứa ít nhất 3 ký tự, không bao gồm ký tự đặc biệt.");
            hasErrors = true;
        }

        if (accountService.isEmailExist(account.getEmail())) {
            result.rejectValue("email", "error.account", "Email đã tồn tại.");
            hasErrors = true;
        } else if (!account.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            result.rejectValue("email", "error.account", "Email không đúng định dạng.");
            hasErrors = true;
        }

        if (account.getPhoneNumber() == null || !account.getPhoneNumber().matches("^\\d{10,15}$")) {
            result.rejectValue("phoneNumber", "error.account", "Số điện thoại phải là số, từ 10 đến 15 ký tự.");
            hasErrors = true;
        }

        if (account.getPassword() == null || !account.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$")) {
            result.rejectValue("password", "error.account", "Mật khẩu phải có ít nhất 6 ký tự, gồm chữ thường, chữ hoa và ký tự đặc biệt.");
            hasErrors = true;
        }

        return hasErrors;
    }

    private void handleImageUpload(Account account, MultipartFile imageFile, String defaultImage) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uploadDir = "src/main/resources/static/Image/imageProfile/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                account.setImageUrl(fileName);
            } catch (IOException e) {
                throw new IOException("Không thể lưu tệp hình ảnh: " + fileName, e);
            }
        } else {
            account.setImageUrl(defaultImage);
        }
    }

    private void validateAndUpdateEmail(Account account, Account existingAccount, BindingResult result, Model model) {
        if (account.getEmail() != null && !account.getEmail().equals(existingAccount.getEmail())) {
            if (accountService.isEmailExist(account.getEmail())) {
                result.rejectValue("email", "error.account", "Email đã tồn tại.");
                model.addAttribute("roles", roleRepository.findAll());
            }
        } else {
            account.setEmail(existingAccount.getEmail());
        }
    }
}
