package bg.fmi.repository;

import bg.fmi.models.SystemConfiguration;
import bg.fmi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    Optional<SystemConfiguration> findByNameAndOwner(String name, User owner);

    List<SystemConfiguration> findByOwner(User owner);

    Optional<SystemConfiguration> findByName(String name);

    Optional<SystemConfiguration> findByNameAndUsers(String name, User user);

    List<SystemConfiguration> findByUsers(User user);
}
