package bg.fmi.controllers;

import bg.fmi.models.User;
import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.ProjectRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.response.ProjectResponse;
import bg.fmi.services.ProjectService;
import bg.fmi.services.UserService;
import bg.fmi.vaultmanagerclient.component.VaultManagerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    private VaultManagerProvider vaultManagerProvider;

    @InjectMocks
    private ProjectController projectController;


    @Test
    public void testGetAll() {
        Set<ProjectResponse> projectResponses = new HashSet<>();
        when(projectService.getAll(any())).thenReturn(projectResponses);
        when(userService.getUser()).thenReturn(new User());

        ResponseEntity<Set<ProjectResponse>> response = projectController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectResponses, response.getBody());
        verify(projectService).getAll(any());
        verify(userService).getUser();
    }

    @Test
    public void testCreate() {
        ProjectRequest project = new ProjectRequest("Test Project", "Test Description");
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = projectController.create(project);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).create(project, user);
        verify(userService).getUser();
    }

    @Test
    void testDeleteSuccess() {
        String projectName = "testProject";
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = projectController.delete(projectName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).delete(projectName, user);
        verify(userService).getUser();
    }

    @Test
    void testAddUsersSuccess() {
        UsersRequest usersRequest = new UsersRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = projectController.addUsers(usersRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).addUsers(usersRequest, user);
        verify(userService).getUser();
    }

    @Test
    void testRemoveUsersSuccess() {
        UsersRequest usersRequest = new UsersRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = projectController.removeUsers(usersRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).removeUsers(usersRequest, user);
        verify(userService).getUser();
    }

    @Test
    void testGetConfigsSuccess() {
        String projectName = "testProject";
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<ProjectResponse> response = projectController.getConfigs(projectName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).getConfiguration(projectName, user);
        verify(userService).getUser();
    }

    @Test
    void testUpdateConfigsSuccess() {
        ConfigsRequest configsRequest = new ConfigsRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = projectController.updateConfigs(configsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService).updateConfigs(configsRequest, user);
        verify(userService).getUser();
    }
}