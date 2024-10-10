//package devmagic.Service;
//
//import devmagic.Model.Account;
//import devmagic.Model.Role;
//import devmagic.Repository.AccountRepository;
//import devmagic.Repository.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AccountService {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    public Account addAccount(Account account) {
//        // Kiểm tra role có tồn tại không
//        Role role = roleRepository.findById(account.getRole().getRoleId())
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//        account.setRole(role); // Gán role cho account
//        return accountRepository.save(account); // Lưu tài khoản
//    }
//}
//
