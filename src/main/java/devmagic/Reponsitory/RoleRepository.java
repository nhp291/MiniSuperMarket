package devmagic.Reponsitory;

import devmagic.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role, Integer> {

    default Role findByName(String name) {
        return null;
    }

    Optional<Role> findByRoleName(String roleName);
}
