package devmagic.Reponsitory;

import devmagic.Model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Page<Account> findAll(Pageable pageable);

    // Tìm tài khoản theo email
    Optional<Account> findByEmail(String email);
    long count();
    // Tìm tài khoản qua username hoặc email
    Optional<Account> findByUsernameOrEmail(String username, String email);

    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT a.accountId FROM Account a WHERE a.username = :username")
    Integer findAccountIdByUsername(@Param("username") String username);
}
