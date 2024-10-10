package devmagic.Repository;

import devmagic.Model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProviderRepository extends JpaRepository<AuthProvider, Integer> {
}
