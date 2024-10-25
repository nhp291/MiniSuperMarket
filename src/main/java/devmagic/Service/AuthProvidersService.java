package devmagic.Service;

import devmagic.Model.AuthProviders;
import devmagic.Reponsitory.AuthProvidersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthProvidersService {
    @Autowired
    private AuthProvidersRepository authProvidersRepository;

    public List<AuthProviders> getAllProviders() {
        return authProvidersRepository.findAll();
    }

    public AuthProviders createProvider(AuthProviders provider) {
        return authProvidersRepository.save(provider);
    }

    public AuthProviders updateProvider(Integer id, AuthProviders provider) {
        provider.setProviderId(id);
        return authProvidersRepository.save(provider);
    }

    public void deleteProvider(Integer id) {
        authProvidersRepository.deleteById(id);
    }
}

