package bg.fmi.services;

import bg.fmi.exceptions.AccessDeniedException;
import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.models.Configuration;
import bg.fmi.models.Project;
import bg.fmi.models.User;
import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.request.ProjectRequest;
import bg.fmi.payload.response.ProjectResponse;
import bg.fmi.repository.ConfigurationRepository;
import bg.fmi.repository.ProjectRepository;
import bg.fmi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private UserRepository userRepository;

    public Set<String> getAllNames(User user) {
        List<String> projectsAsUSer = projectRepository.findByUsers(user).stream().map(Project::getName).collect(Collectors.toList());
        List<String> projectsAsOwner = projectRepository.findByOwner(user).stream().map(Project::getName).collect(Collectors.toList());
        Set<String> allProjectNAmes = new HashSet<>();
        allProjectNAmes.addAll(projectsAsOwner);
        allProjectNAmes.addAll(projectsAsUSer);
        return allProjectNAmes;
    }

    public void create(ProjectRequest projectRequest, User owner) {
        projectRepository.save(
                Project.builder()
                        .name(projectRequest.getName())
                        .users(Set.of(owner))
                        .owner(owner)
                        .build());
    }

    public void changeOwner(ProjectRequest projectRequest, User owner) {
        Project project = projectRepository.findByName(projectRequest.getName())
                .orElseThrow(NoDataFoundException::new);
        project.setOwner(owner);
        project.setUsers(new HashSet<>());
        projectRepository.save(project);
    }

    public void addUsers(UsersRequest usersRequest, User owner) {
        Project project = projectRepository.findByNameAndOwner(usersRequest.getProjectName(), owner)
                .orElseThrow(NoDataFoundException::new);
        Set<User> currentUsers = project.getUsers();

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUserNames());

        currentUsers.addAll(new HashSet<>(dbUsers));
        project.setUsers(currentUsers);
        projectRepository.save(project);
    }

    public void removeUsers(UsersRequest usersRequest, User owner) {
        Project project = projectRepository.findByNameAndOwner(usersRequest.getProjectName(), owner)
                .orElseThrow(NoDataFoundException::new);
        Set<User> currentUsers = project.getUsers();

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUserNames());

        currentUsers.removeAll(new HashSet<>(dbUsers));
        project.setUsers(currentUsers);
        projectRepository.save(project);
    }

    public ProjectResponse getConfiguration(String projectName, User user) {
        Project project = projectRepository.findByNameAndUsers(projectName, user)
                .orElseThrow(AccessDeniedException::new);

        Map<String, String> configs = new HashMap<>();
        project.getConfigurations().forEach(configuration -> configs.put(configuration.getName(), configuration.getValue()));

        return ProjectResponse.builder()
                .name(project.getName())
                .username(project.getUsers().stream().map(User::getUsername).collect(Collectors.toList()))
                .configurations(configs)
                .build();
    }

    public void updateConfigs(ConfigsRequest configsRequest, User user) {
        Project project = projectRepository.findByNameAndUsers(configsRequest.getProjectName(), user)
                .orElseThrow(NoDataFoundException::new);

        configurationRepository.deleteByProject(project);

        List<Configuration> configurations = new ArrayList<>();
        configsRequest.getConfigs().forEach((name, value) ->
                configurations.add(
                        Configuration.builder()
                                .name(name)
                                .value(value)
                                .project(project)
                                .build()));
        project.setConfigurations(configurations);

        projectRepository.save(project);
    }
}
