package devmagic.Reponsitory;

import devmagic.Model.AuthProviders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProvidersRepository extends JpaRepository<AuthProviders, Integer> {
}

