package devmagic.Reponsitory;

import devmagic.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Optional<Account> findByEmail(String email);


}
