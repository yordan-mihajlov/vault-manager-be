package bg.fmi.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import bg.fmi.models.User;
import bg.fmi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testLoadUserByUsernameWhenUserExistsReturnsUserDetails() {
        // Given
        User user = new User("username", "email@email.com", "password", "firstname", "lastname");
        Mockito.when(userRepository.findByUsername("username"))
                .thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("username");

        // Then
        assertThat(userDetails.getUsername()).isEqualTo("username");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    public void testLoadUserByUsernameWhenUserNotExistsThrowsException() {
        // Given
        Mockito.when(userRepository.findByUsername("username"))
                .thenReturn(Optional.empty());

        // When
        Throwable exception = catchThrowable(() -> userDetailsService.loadUserByUsername("username"));

        // Then
        assertThat(exception).isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User Not Found with username: username");
    }
}
