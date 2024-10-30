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
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void saveAccount(Account account) {
        // Kiểm tra role xem đã được chọn chưa
        if (account.getRole() != null) {
            Role role = roleRepository.findById(account.getRole().getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + account.getRole().getRoleId()));
            account.setRole(role); // Gán lại role từ cơ sở dữ liệu
        }
        accountRepository.save(account);
    }

    public Account getAccountById(Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    public void deleteAccount(Integer id) {
        accountRepository.deleteById(id);
    }

}
