package devmagic.Controller.Admin;

import devmagic.Model.Account;
import devmagic.Reponsitory.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    // Lấy tất cả tài khoản
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Lấy thông tin tài khoản theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable int id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thêm tài khoản mới
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    // Cập nhật tài khoản
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable int id, @RequestBody Account accountDetails) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            account.setUsername(accountDetails.getUsername());
            account.setPassword(accountDetails.getPassword());
            account.setEmail(accountDetails.getEmail());
            account.setPhoneNumber(accountDetails.getPhoneNumber());
            account.setAddress(accountDetails.getAddress());
            account.setImageUrl(accountDetails.getImageUrl());
            account.setRole(accountDetails.getRole());

            Account updatedAccount = accountRepository.save(account);
            return ResponseEntity.ok(updatedAccount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa tài khoản
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        accountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
