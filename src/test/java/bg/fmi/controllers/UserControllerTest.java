package bg.fmi.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import bg.fmi.models.ERole;
import bg.fmi.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testMarkUserAsAdmin() {
        String username = "johndoe";

        userController.markUserAsAdmin(username);

        verify(userService, times(1)).markUserAsAdmin(username);
    }

    @Test
    public void testGetUsernames() {
        List<String> usernames = Arrays.asList("johndoe", "janedoe");
        when(userService.getUsernames()).thenReturn(usernames);

        ResponseEntity<List<String>> response = userController.getUsernames();

        assertEquals(usernames, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetUsernamesByRoles() {
        List<String> usernames = Arrays.asList("johndoe", "janedoe");
        Set<ERole> roles = Set.of(ERole.ROLE_ADMIN, ERole.ROLE_USER);
        when(userService.getUsernames(roles)).thenReturn(usernames);

        ResponseEntity<List<String>> response = userController.getUsernamesByRoles(roles.toArray(new ERole[roles.size()]));

        assertEquals(usernames, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}