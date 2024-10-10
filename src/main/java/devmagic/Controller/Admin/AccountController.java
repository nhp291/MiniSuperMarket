package devmagic.Controller.Admin;

import devmagic.Model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin("*")
public class AccountController {

//    @Autowired
//    private AccountService accountService;

    @GetMapping()
    public ResponseEntity<String> getAllAccounts() {
//        Account newAccount = accountService.addAccount(account);
        return ResponseEntity.ok("Lấy dữ liệu thành công!");
    }
}

