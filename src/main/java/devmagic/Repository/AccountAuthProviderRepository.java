package devmagic.Repository;

import devmagic.Model.AccountAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAuthProviderRepository extends JpaRepository<AccountAuthProvider, Integer> {
}
