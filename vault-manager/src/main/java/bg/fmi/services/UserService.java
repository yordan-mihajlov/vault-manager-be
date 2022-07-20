package bg.fmi.services;

import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.models.*;
import bg.fmi.repository.ProjectRepository;
import bg.fmi.repository.RoleRepository;
import bg.fmi.repository.UserRepository;
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

    public void markUserAsAdmin(String username) {
        User dbUser = userRepository.findByUsername(username).orElseThrow(NoDataFoundException::new);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());
        dbUser.setRoles(roles);
        userRepository.save(dbUser);
    }

    public List<String> getUsernames() {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).get());
        return userRepository.findByRolesIn(roles).stream().map(User::getUsername).collect(Collectors.toList());
    }

    public List<String> getUsernames(Set<ERole> eRoles) {
        Set<Role> roles = roleRepository.findByNameIn(eRoles);
        return userRepository.findByRolesIn(roles).stream().map(User::getUsername).collect(Collectors.toList());
    }
}
