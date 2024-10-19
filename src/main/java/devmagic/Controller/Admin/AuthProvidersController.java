package devmagic.Controller.Admin;

import devmagic.Model.AuthProviders;
import devmagic.Service.AuthProvidersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth-providers")
public class AuthProvidersController {
    @Autowired
    private AuthProvidersService authProvidersService;

    @GetMapping
    public List<AuthProviders> getAllProviders() {
        return authProvidersService.getAllProviders();
    }

    @PostMapping
    public AuthProviders createProvider(@RequestBody AuthProviders provider) {
        return authProvidersService.createProvider(provider);
    }

    @PutMapping("/{id}")
    public AuthProviders updateProvider(@PathVariable Integer id, @RequestBody AuthProviders provider) {
        return authProvidersService.updateProvider(id, provider);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Integer id) {
        authProvidersService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}

