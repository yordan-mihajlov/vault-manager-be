package bg.fmi.services;

import bg.fmi.exceptions.AccessDeniedException;
import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.exceptions.ResourceAlreadyExists;
import bg.fmi.models.*;
import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.request.SystemConfigurationRequest;
import bg.fmi.payload.response.ConfigResponse;
import bg.fmi.repositories.ConfigurationRepository;
import bg.fmi.repositories.SystemConfigurationRepository;
import bg.fmi.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
public class ConfigService {

    private static Logger log = Logger.getLogger(ConfigService.class.getName());

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private UserRepository userRepository;

    public Set<ConfigResponse> getAll(User user) {
        Set<ConfigResponse> allConfig = new HashSet<>();
        List<SystemConfiguration> configsAsUSer = systemConfigurationRepository.findByUsers(user);
        List<SystemConfiguration> configsAsOwner = systemConfigurationRepository.findByOwner(user);

        configsAsUSer.forEach(systemConfiguration -> {
            allConfig.add(
                    ConfigResponse.builder().name(systemConfiguration.getName()).description(systemConfiguration.getDescription()).build());
        });
        configsAsOwner.forEach(systemConfiguration -> {
            allConfig.add(
                    ConfigResponse.builder().name(systemConfiguration.getName()).description(systemConfiguration.getDescription()).build());
        });


        log.info(String.format("User %s fetched all usernames", user.getUsername()));

        return allConfig;
    }

    public void create(SystemConfigurationRequest systemConfigurationRequest, User owner) {
        Optional<SystemConfiguration> config = systemConfigurationRepository.findByName(systemConfigurationRequest.getName());

        if (config.isPresent()) {
            throw new ResourceAlreadyExists();
        }

        Set<User> users = new HashSet<>();
        users.add(owner);
        systemConfigurationRepository.save(
                SystemConfiguration.builder()
                        .name(systemConfigurationRequest.getName())
                        .description(systemConfigurationRequest.getDescription())
                        .users(users)
                        .owner(owner)
                        .build());

        log.info(String.format("User %s created a new systemConfiguration %s", owner.getUsername(), systemConfigurationRequest.getName()));
    }

    public void delete(String configName, User owner) {
        Optional<SystemConfiguration> config = systemConfigurationRepository.findByNameAndOwner(configName, owner);

        if (!config.isPresent()) {
            throw new NoDataFoundException();
        }

        systemConfigurationRepository.delete(config.get());

        log.info(String.format("User %s deleted a systemConfiguration %s", owner.getUsername(), configName));
    }

    public void changeOwner(SystemConfigurationRequest systemConfigurationRequest, User owner) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByName(systemConfigurationRequest.getName())
                .orElseThrow(NoDataFoundException::new);
        systemConfiguration.setOwner(owner);
        systemConfiguration.setUsers(new HashSet<>());
        systemConfigurationRepository.save(systemConfiguration);
    }

    public void addUsers(UsersRequest usersRequest, User owner) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndOwner(usersRequest.getConfigName(), owner)
                .orElseThrow(NoDataFoundException::new);
        Set<User> currentUsers = systemConfiguration.getUsers();

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUsernames());

        currentUsers.addAll(new HashSet<>(dbUsers));
        systemConfiguration.setUsers(currentUsers);
        systemConfigurationRepository.save(systemConfiguration);

        final String users = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s added %s to systemConfiguration %s",
                owner.getUsername(), users, usersRequest.getConfigName()));
    }

    public void changeUsers(UsersRequest usersRequest, User owner) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndOwner(usersRequest.getConfigName(), owner)
                .orElseThrow(NoDataFoundException::new);

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUsernames());
        dbUsers.add(owner);

        Set<User> users = systemConfiguration.getUsers().stream()
                .filter(configUser -> configUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ERole.ROLE_SYSTEM)
                ).collect(Collectors.toSet());
        users.addAll(dbUsers);

        systemConfiguration.setUsers(users);
        systemConfigurationRepository.save(systemConfiguration);

        final String usernames = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s change %s to systemConfiguration %s",
                owner.getUsername(), usernames, usersRequest.getConfigName()));
    }

    public void changeSystems(UsersRequest usersRequest, User owner) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndOwner(usersRequest.getConfigName(), owner)
                .orElseThrow(NoDataFoundException::new);

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUsernames());
        dbUsers.add(owner);
        Set<User> users = systemConfiguration.getUsers().stream()
                .filter(configUser -> configUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ERole.ROLE_USER)
        ).collect(Collectors.toSet());
        users.addAll(dbUsers);

        systemConfiguration.setUsers(users);
        systemConfigurationRepository.save(systemConfiguration);

        final String usernames = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s change %s to systemConfiguration %s",
                owner.getUsername(), usernames, usersRequest.getConfigName()));
    }

    public void removeUsers(UsersRequest usersRequest, User owner) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndOwner(usersRequest.getConfigName(), owner)
                .orElseThrow(NoDataFoundException::new);
        Set<User> currentUsers = systemConfiguration.getUsers();

        List<User> dbUsers = userRepository.findByUsernameIn(usersRequest.getUsernames());

        currentUsers.removeAll(new HashSet<>(dbUsers));
        systemConfiguration.setUsers(currentUsers);
        systemConfigurationRepository.save(systemConfiguration);

        final String users = dbUsers.stream().map(User::getUsername)
                .collect(joining(") (", "(", ")"));
        log.info(String.format("User %s removed %s from systemConfiguration %s",
                owner.getUsername(), users, usersRequest.getConfigName()));
    }

    public ConfigResponse getConfiguration(String configName, User user) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndUsers(configName, user)
                .orElseThrow(AccessDeniedException::new);

        Map<String, String> configs = new HashMap<>();
        systemConfiguration.getConfigurations().forEach(configuration ->
                configs.put(configuration.getName(), configuration.getValue()));

        log.info(String.format("User %s fetched systemConfiguration for systemConfiguration %s", user.getUsername(), configName));

        return ConfigResponse.builder()
                .description(systemConfiguration.getDescription())
                .name(systemConfiguration.getName())
                .usernames(systemConfiguration.getUsers().stream()
                        .filter(configUser ->
                             configUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ERole.ROLE_USER)
                        )
                        .map(User::getUsername).collect(Collectors.toList()))
                .systemnames(systemConfiguration.getUsers().stream()
                        .filter(configUser ->
                                configUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(ERole.ROLE_SYSTEM)
                        )
                        .map(User::getUsername).collect(Collectors.toList()))
                .configurations(configs)
                .build();
    }

    public void updateConfigs(ConfigsRequest configsRequest, User user) {
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findByNameAndUsers(configsRequest.getConfigName(), user)
                .orElseThrow(NoDataFoundException::new);

        configurationRepository.deleteBySystemConfiguration(systemConfiguration);

        List<Configuration> configurations = new ArrayList<>();
        configsRequest.getConfigs().forEach((name, value) ->
                configurations.add(
                        Configuration.builder()
                                .name(name)
                                .value(value)
                                .systemConfiguration(systemConfiguration)
                                .build()));
        systemConfiguration.setConfigurations(configurations);

        systemConfigurationRepository.save(systemConfiguration);

        log.info(String.format("User %s updated systemConfiguration for systemConfiguration %s",
                user.getUsername(), configsRequest.getConfigName()));
    }
}
