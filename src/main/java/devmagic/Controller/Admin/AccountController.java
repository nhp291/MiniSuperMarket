package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.RoleRepository;
import devmagic.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
@Controller
@RequestMapping("/Accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/AccountList")
    public String AccountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        model.addAttribute("pageTitle", "Account List Page");
        model.addAttribute("viewName", "admin/menu/AccountList");
        return "admin/layout";
    }

    @GetMapping("/AddAccount")
    public String AddAccount(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Add Account Page");
        model.addAttribute("viewName", "admin/menu/AddAccount");
        return "admin/layout";
    }


    // Hiển thị form thêm tài khoản
    @GetMapping("/add")
    public String showAddAccountForm(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("roles", roleRepository.findAll()); // Đổ danh sách role vào form
        return "addAccount"; // Trỏ đến file Thymeleaf addAccount.html
    }

    // Xử lý form thêm tài khoản
//    @PostMapping("/add")
//    public String addAccount(@ModelAttribute("account") Account account) {
//        accountService.saveAccount(account);
//        return "redirect:/accounts/list";
//    }

    @GetMapping("/edit/{id}")
    public String showEditAccountForm(@PathVariable("id") Integer id, Model model) {
        Account account = accountService.getAccountById(id);
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Edit Account Page");
        return "admin/menu/AddAccount"; // Chuyển đến template addAccount.html
    }

    // Xử lý form cập nhật tài khoản
    @PostMapping("/update")
    public String updateAccount(@ModelAttribute("account") Account account) {
        accountService.saveAccount(account); // Lưu thông tin tài khoản đã cập nhật
        return "redirect:/Accounts/AccountList"; // Chuyển hướng về danh sách tài khoản
    }

    // Xóa tài khoản
    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        accountService.deleteAccount(id);
        return "redirect:/Accounts/AccountList"; // Chuyển hướng về danh sách tài khoản
    }

}
