package devmagic.Repository;

import devmagic.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    // Tùy chọn thêm query method nếu cần
}
