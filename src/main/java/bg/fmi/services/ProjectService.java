package bg.fmi.services;

import bg.fmi.exceptions.AccessDeniedException;
import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.exceptions.ResourceAlreadyExists;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
public class ProjectService {

    private static Logger log = Logger.getLogger(ProjectService.class.getName());

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private UserRepository userRepository;

    public Set<ProjectResponse> getAll(User user) {
        Set<ProjectResponse> allProject = new HashSet<>();
        List<Project> projectsAsUSer = projectRepository.findByUsers(user);
        List<Project> projectsAsOwner = projectRepository.findByOwner(user);

        projectsAsUSer.forEach(project -> {
            allProject.add(
                    ProjectResponse.builder().name(project.getName()).description(project.getDescription()).build());
        });
        projectsAsOwner.forEach(project -> {
            allProject.add(
                    ProjectResponse.builder().name(project.getName()).description(project.getDescription()).build());
        });


        log.info(String.format("User %s fetched all usernames", user.getUsername()));

        return allProject;
    }

    public void create(ProjectRequest projectRequest, User owner) {
        Optional<Project> project = projectRepository.findByName(projectRequest.getName());

        if (project.isPresent()) {
            throw new ResourceAlreadyExists();
        }

        projectRepository.save(
                Project.builder()
                        .name(projectRequest.getName())
                        .description(projectRequest.getDescription())
                        .users(Set.of(owner))
                        .owner(owner)
                        .build());

        log.info(String.format("User %s created a new project %s", owner.getUsername(), projectRequest.getName()));
    }

    public void delete(String projectName, User owner) {
        Optional<Project> project = projectRepository.findByNameAndOwner(projectName, owner);

        if (!project.isPresent()) {
            throw new NoDataFoundException();
        }

        projectRepository.delete(project.get());

        log.info(String.format("User %s deleted a project %s", owner.getUsername(), projectName));
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

        final String users = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s added %s to project %s", 
                owner.getUsername(), users, usersRequest.getProjectName()));
    }

    public void removeUsers(UsersRequest usersRequest, User owner) {
        Project project = projectRepository.findByNameAndOwner(usersRequest.getProjectName(), owner)
                .orElseThrow(NoDataFoundException::new);
        Set<User> currentUsers = project.getUsers();

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUserNames());

        currentUsers.removeAll(new HashSet<>(dbUsers));
        project.setUsers(currentUsers);
        projectRepository.save(project);

        final String users = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s removed %s from project %s", 
                owner.getUsername(), users, usersRequest.getProjectName()));
    }

    public ProjectResponse getConfiguration(String projectName, User user) {
        Project project = projectRepository.findByNameAndUsers(projectName, user)
                .orElseThrow(AccessDeniedException::new);

        Map<String, String> configs = new HashMap<>();
        project.getConfigurations().forEach(configuration -> 
                configs.put(configuration.getName(), configuration.getValue()));

        log.info(String.format("User %s fetched config for project %s", user.getUsername(), projectName));

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

        log.info(String.format("User %s updated config for project %s", 
                user.getUsername(), configsRequest.getProjectName()));
    }
}
