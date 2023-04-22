package bg.fmi.services;

import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.models.*;
import bg.fmi.payload.response.RegisterResponse;
import bg.fmi.repositories.SystemConfigurationRepository;
import bg.fmi.repositories.RoleRepository;
import bg.fmi.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static Logger log = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username).orElseThrow(NoDataFoundException::new);
    }

    public RegisterResponse getUserDetails() {
        User user = getUser();

        return RegisterResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .roles((user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList())))
                .build();
    }

    public void markUsersAsAdmins(List<String> usernames) {
        List<User> dbUsers = userRepository.findByUsernameIn(usernames);
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();

        dbUsers.forEach(user -> {
            Set<Role> roles = user.getRoles();
            roles.add(adminRole);
            user.setRoles(roles);
        });

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).get();
        List<User> dbAdmins = userRepository.findByRolesIn(adminRoles);
        dbAdmins.forEach(admin -> {
            if(!admin.getUsername().equals(getUser().getUsername())) {
                Set<Role> roles = admin.getRoles();
                roles.remove(adminRole);
                roles.add(userRole);
                admin.setRoles(roles);
            }
        });

        userRepository.saveAll(dbUsers);

        log.info(String.format("Users %s was marked as admin", usernames));
        log.info(String.format("Users %s was marked as users", dbAdmins.stream().map(User::getUsername).collect(Collectors.toList())));
    }

    public List<String> getUsernames() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER).get());
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByRolesIn(roles).stream().map(User::getUsername)
                .filter(username -> !username.equals(authentication.getName())).collect(Collectors.toList());
    }

    public List<String> getUsernames(ERole eRole) {
        Role role = roleRepository.findByName(eRole).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByRolesIn(Set.of(role)).stream().map(User::getUsername)
                .filter(username -> !username.equals(authentication.getName())).collect(Collectors.toList());
    }
}
