package devmagic.Controller.Admin;

import devmagic.Model.AccountAuthProvider;
import devmagic.Service.AccountAuthProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-auth-providers")
public class AccountAuthProviderController {
    @Autowired
    private AccountAuthProviderService accountAuthProviderService;

    @GetMapping
    public List<AccountAuthProvider> getAllProviders() {
        return accountAuthProviderService.getAllProviders();
    }

    @PostMapping
    public AccountAuthProvider createProvider(@RequestBody AccountAuthProvider provider) {
        return accountAuthProviderService.createProvider(provider);
    }

    @PutMapping("/{id}")
    public AccountAuthProvider updateProvider(@PathVariable Integer id, @RequestBody AccountAuthProvider provider) {
        return accountAuthProviderService.updateProvider(id, provider);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Integer id) {
        accountAuthProviderService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}
