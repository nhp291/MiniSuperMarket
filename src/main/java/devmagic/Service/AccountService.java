package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.AccountRepository;
import devmagic.Reponsitory.RoleRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    // Lấy tài khoản theo username (hoặc email)
    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void saveAccount(Account account) {
        if (account == null) {
            logger.error("Account cannot be null!");
            throw new IllegalArgumentException("Account cannot be null");
        }

        // Assign default role if none is provided
        if (account.getRole() == null) {
            Role defaultRole = roleRepository.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("Default role not found!"));
            account.setRole(defaultRole);
        }

        // Ensure password is retained for existing accounts
        if (account.getAccountId() != null) {
            accountRepository.findById(account.getAccountId())
                    .ifPresent(existingAccount -> {
                        if (account.getPassword() == null || account.getPassword().isEmpty()) {
                            account.setPassword(existingAccount.getPassword());
                        }
                    });
        }

        accountRepository.save(account);
        logger.info("Account saved successfully: {}", account.getUsername());
    }


    public Optional<Account> getAccountById(Integer id) {
        if (id == null) {
            logger.warn("ID không được để null!");
            return Optional.empty();
        }
        return accountRepository.findById(id);
    }

    public void deleteAccount(Integer id) {
        if (id == null) {
            logger.error("ID không được để null khi xóa tài khoản!");
            throw new IllegalArgumentException("ID không được để null");
        }
        accountRepository.deleteById(id);
        logger.info("Account với ID {} đã được xóa thành công", id);
    }

    public long getTotalAccounts() {
        return accountRepository.count();
    }

    public boolean isUsernameExist(String username) {
        if (username == null || username.isEmpty()) {
            logger.warn("Username không được để trống khi kiểm tra!");
            return false;
        }
        return accountRepository.existsByUsername(username);
    }

    public boolean isEmailExist(String email) {
        if (email == null || email.isEmpty()) {
            logger.warn("Email không được để trống khi kiểm tra!");
            return false;
        }
        return accountRepository.existsByEmail(email);
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
        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            logger.warn("Cả username và email không được để trống khi kiểm tra!");
            return false;
        }
        return accountRepository.existsByUsernameOrEmail(username, email);
    }



}
