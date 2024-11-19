package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.AccountRepository;
import devmagic.Reponsitory.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void saveAccount(Account account) {
        if (account.getRole() != null) {
            Role role = roleRepository.findById(account.getRole().getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + account.getRole().getRoleId()));
            account.setRole(role);
        }

        System.out.println("Saving account: " + account.toString());

        accountRepository.save(account);
    }

    public Account getAccountById(Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }

    public long getTotalAccounts() {
        return accountRepository.count();  // Trả về số lượng Account
    }

    public void saveAccountUser(Account account) {
        // Kiểm tra username hoặc email đã tồn tại chưa
        if (accountRepository.existsByUsernameOrEmail(account.getUsername(), account.getEmail())) {
            throw new IllegalArgumentException("Username hoặc email đã tồn tại!");
        }

        // Tìm Role mặc định từ cơ sở dữ liệu (roleId = 2 cho ROLE_USER)
        Role defaultRole = roleRepository.findById(2)
                .orElseThrow(() -> new IllegalArgumentException("Role mặc định không tồn tại!"));

        // Gán Role mặc định nếu chưa được thiết lập
        if (account.getRole() == null) {
            account.setRole(defaultRole); // Gán đối tượng Role vào tài khoản
        }

        // Nếu imageUrl không có, đặt NULL
        if (account.getImageUrl() == null || account.getImageUrl().isEmpty()) {
            account.setImageUrl(null);
        }

        // Lưu tài khoản vào cơ sở dữ liệu
        accountRepository.save(account);
    }
    public boolean isUsernameOrEmailExist(String username, String email) {
        return accountRepository.existsByUsernameOrEmail(username, email);
    }

}
