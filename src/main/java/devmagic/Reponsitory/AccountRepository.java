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
    // Tìm tài khoản theo username
    Optional<Account> findByUsername(String username);

    // Tìm tài khoản theo email
    Optional<Account> findByEmail(String email);

    // Custom Query: Kiểm tra xem username hoặc email có tồn tại
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Account a WHERE a.username = :username OR a.email = :email")
    boolean existsByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    long count();

}
