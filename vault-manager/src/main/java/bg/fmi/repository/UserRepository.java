package bg.fmi.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import bg.fmi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  List<User> findByRolesIn(Set<Role> roles);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  List<User> findByUsernameIn(List<String> userNames);
}
