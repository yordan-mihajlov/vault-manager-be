package bg.fmi.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import bg.fmi.models.ERole;
import bg.fmi.models.Role;
import bg.fmi.models.User;
import bg.fmi.repository.ProjectRepository;
import bg.fmi.repository.RoleRepository;
import bg.fmi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private String username;
    private Set<ERole> eRoles;
    private Set<Role> roles;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        username = "testuser";
        user.setUsername(username);
        eRoles = new HashSet<>();
        eRoles.add(ERole.ROLE_USER);
        roles = new HashSet<>();
        role = new Role();
        role.setName(ERole.ROLE_USER);
        roles.add(role);
        user.setRoles(roles);
    }

    @Test
    void testGetUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUser();

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testMarkUserAsAdmin() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));

        userService.markUserAsAdmin(username);

        assertEquals(ERole.ROLE_USER, user.getRoles().iterator().next().getName());
        verify(userRepository, times(1)).findByUsername(username);
        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUsernamesShouldReturnListOfUsernames() {
        // Given
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setRoles(Set.of(adminRole));

        User user2 = new User();
        user2.setUsername("user2");
        user2.setRoles(Set.of(adminRole));

        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRolesIn(Set.of(adminRole))).thenReturn(List.of(user1, user2));

        // When
        List<String> usernames = userService.getUsernames();

        // Then
        assertThat(usernames).containsExactly("user1", "user2");
    }

    @Test
    public void testGetUsernamesShouldReturnEmptyListIfNoUsersWithAdminRole() {
        // Given
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);

        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRolesIn(Set.of(adminRole))).thenReturn(Collections.emptyList());

        // When
        List<String> usernames = userService.getUsernames();

        // Then
        assertThat(usernames).isEmpty();
    }

    @Test
    public void testGetUsernames_whenNoUsersExist_thenReturnEmptyList() {
        // given
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(new Role(1, ERole.ROLE_ADMIN)));
        when(userRepository.findByRolesIn(Collections.singleton(new Role(1, ERole.ROLE_ADMIN)))).thenReturn(Collections.emptyList());

        // when
        List<String> usernames = userService.getUsernames();

        // then
        assertEquals(Collections.emptyList(), usernames);
    }

    @Test
    public void testGetUsernames_whenUsersExist_thenReturnUsernames() {
        // given
        Role role = new Role(1, ERole.ROLE_ADMIN);
        User user1 = new User(1L, "username1", "email", "password", Collections.singleton(role));
        User user2 = new User(2L, "username2", "email", "password", Collections.singleton(role));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(role));
        when(userRepository.findByRolesIn(Collections.singleton(role))).thenReturn(Arrays.asList(user1, user2));

        // when
        List<String> usernames = userService.getUsernames();

        // then
        assertEquals(Arrays.asList("username1", "username2"), usernames);
    }
}
