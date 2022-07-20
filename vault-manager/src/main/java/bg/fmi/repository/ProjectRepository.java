package bg.fmi.repository;

import bg.fmi.models.Project;
import bg.fmi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByNameAndOwner(String name, User owner);

    List<Project> findByOwner(User owner);

    Optional<Project> findByName(String name);

    Optional<Project> findByNameAndUsers(String name, User user);

    List<Project> findByUsers(User user);
}
