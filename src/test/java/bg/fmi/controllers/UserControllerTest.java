package bg.fmi.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import bg.fmi.models.ERole;
import bg.fmi.payload.request.MarkUsersAsAdminsRequest;
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
        MarkUsersAsAdminsRequest markUsersAsAdminsRequest = new MarkUsersAsAdminsRequest();
        markUsersAsAdminsRequest.setUsernames(List.of(username));

        userController.markUsersAsAdmins(markUsersAsAdminsRequest);

        verify(userService, times(1)).markUsersAsAdmins(List.of(username));
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
        ERole role = ERole.ROLE_ADMIN;
        when(userService.getUsernames(role)).thenReturn(usernames);

        ResponseEntity<List<String>> response = userController.getUsernamesByRoles(role);

        assertEquals(usernames, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}