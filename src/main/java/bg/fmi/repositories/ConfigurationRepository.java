package bg.fmi.repositories;

import bg.fmi.models.Configuration;
import bg.fmi.models.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    void deleteBySystemConfiguration(SystemConfiguration systemConfiguration);
}