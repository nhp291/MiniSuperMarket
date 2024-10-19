package devmagic.Service;

import devmagic.Model.AccountAuthProvider;
import devmagic.Reponsitory.AccountAuthProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountAuthProviderService {
    @Autowired
    private AccountAuthProviderRepository accountAuthProviderRepository;

    public List<AccountAuthProvider> getAllProviders() {
        return accountAuthProviderRepository.findAll();
    }

    public AccountAuthProvider createProvider(AccountAuthProvider provider) {
        return accountAuthProviderRepository.save(provider);
    }

    public AccountAuthProvider updateProvider(Integer id, AccountAuthProvider provider) {
        provider.setId(id);
        return accountAuthProviderRepository.save(provider);
    }

    public void deleteProvider(Integer id) {
        accountAuthProviderRepository.deleteById(id);
    }
}

