package bg.fmi.repository;

import bg.fmi.models.Configuration;
import bg.fmi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    void deleteByProject(Project project);
}