package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.AccountRepository;
import devmagic.Reponsitory.RoleRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public boolean checkLogin(String username, String password) {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isPresent()) {
            boolean match = passwordEncoder.matches(password, account.get().getPassword());
            if (!match) {
                logger.debug("Mật khẩu không đúng cho người dùng: " + username);
            }
            return match;
        }
        logger.debug("Tài khoản không tồn tại: " + username);
        return false;
    }


    // Helper method for password encoding
    private String encodePassword(String password) {
        return passwordEncoder.encode(password); // Sử dụng passwordEncoder đã được inject
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

        // Gán vai trò mặc định nếu chưa có
        if (account.getRole() == null) {
            Role defaultRole = roleRepository.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("Default role not found!"));
            account.setRole(defaultRole);
        }

        // Đảm bảo mã hóa mật khẩu cho tài khoản mới
        if (account.getAccountId() == null) {
            // Mã hóa mật khẩu mới khi tạo tài khoản
            account.setPassword(encodePassword(account.getPassword()));
        } else {
            // Nếu mật khẩu đã thay đổi, mã hóa lại mật khẩu
            accountRepository.findById(account.getAccountId()).ifPresent(existingAccount -> {
                if (account.getPassword() != null && !account.getPassword().isEmpty() &&
                        !account.getPassword().equals(existingAccount.getPassword())) {
                    account.setPassword(encodePassword(account.getPassword()));
                } else {
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
    public Optional<Account> findById(Integer accountId) {
        return accountRepository.findById(accountId);
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

    // Thay thế các mật khẩu này bằng mật khẩu từ cơ sở dữ liệu
    String[] rawPasswords = {"password1", "12345", "mysecret"};

    // Phương thức tạm thời để in ra mật khẩu đã mã hóa
    public void printEncodedPassword(String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Mật khẩu gốc: " + rawPassword);
        System.out.println("Mật khẩu đã mã hóa: " + encodedPassword);
    }

    public Integer findAccountIdByUsername(String username) {
        return accountRepository.findByUsername(username)
                .map(Account::getAccountId) // Assuming Account has a getAccountId method
                .orElse(null); // Return null if the account is not found
    }


}
