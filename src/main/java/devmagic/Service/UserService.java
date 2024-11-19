package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.RoleRepository;
import devmagic.Reponsitory.UserRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    // Khai báo biến userRepository kiểu UserRepository để quản lý dữ liệu người dùng
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Integer findRoleIdByName(String roleName) {
        Role role = roleRepository.findByName(roleName);
        return role != null ? role.getRoleId() : null;
    }
    public Role findRoleById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }


    // Constructor để khởi tạo UserService với userRepository
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Phương thức tìm kiếm người dùng theo tên đăng nhập (username)
    public Account findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Phương thức kiểm tra đăng nhập, trả về true nếu tên đăng nhập và mật khẩu khớp
    public boolean checkLogin(String username, String password) {
        Account account = userRepository.findByUsername(username);
        return account != null && account.getPassword().equals(password);
    }

    // Lấy danh sách tất cả tài khoản người dùng
    public List<Account> getAllAccounts() {
        return userRepository.findAll();
    }

    // Tạo mới một tài khoản người dùng
    public Account createAccount(Account account) {
        return userRepository.save(account);
    }

    // Tìm kiếm id
    public void findbyID(Integer id) {
        userRepository.findById(Long.valueOf(id));
        return;
    }

    // Cập nhật tài khoản người dùng dựa trên id
    public Account updateAccount(Integer id, Account account) {
        account.setAccountId(id);
        return userRepository.save(account);
    }

    // Xóa tài khoản người dùng dựa trên id
    public void deleteAccount(Integer id) {
        userRepository.deleteById(Long.valueOf(id));
    }

    // tìm kiếm email
    public Optional<Account> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public void save(Account existingAccount) {
        userRepository.save(existingAccount);
    }
}
