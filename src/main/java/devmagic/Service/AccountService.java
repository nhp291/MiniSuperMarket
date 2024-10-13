package devmagic.Service;

import devmagic.Model.Account;
import devmagic.Reponsitory.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public ResponseEntity<Account> getAccountById(int id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public ResponseEntity<Account> updateAccount(int id, Account accountDetails) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
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

    public ResponseEntity<Void> deleteAccount(int id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
