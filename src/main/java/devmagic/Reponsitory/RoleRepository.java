package devmagic.Reponsitory;

import devmagic.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role, Integer> {
    default Role findByName(String name) {
        return null;
    }
}
