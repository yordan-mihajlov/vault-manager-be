package bg.fmi.services;

import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.models.*;
import bg.fmi.payload.response.UserInfoResponse;
import bg.fmi.repository.ProjectRepository;
import bg.fmi.repository.RoleRepository;
import bg.fmi.repository.UserRepository;
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
    private ProjectRepository projectRepository;

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username).orElseThrow(NoDataFoundException::new);
    }

    public UserInfoResponse getUserDetails() {
        User user = getUser();

        return UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .roles((user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList())))
                .build();
    }

    public void markUsersAsAdmins(List<String> usernames) {
        List<User> dbUsers = userRepository.findByUsernameIn(usernames);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());
        dbUsers.forEach(user -> user.setRoles(roles));
        userRepository.saveAll(dbUsers);

        log.info(String.format("Users %s was marked as admin", usernames));
    }

    public List<String> getUsernames() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER).get());
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByRolesIn(roles).stream().map(User::getUsername)
                .filter(username -> !username.equals(authentication.getName())).collect(Collectors.toList());
    }

    public List<String> getUsernames(Set<ERole> eRoles) {
        Set<Role> roles = roleRepository.findByNameIn(eRoles);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByRolesIn(roles).stream().map(User::getUsername)
                .filter(username -> !username.equals(authentication.getName())).collect(Collectors.toList());
    }
}
