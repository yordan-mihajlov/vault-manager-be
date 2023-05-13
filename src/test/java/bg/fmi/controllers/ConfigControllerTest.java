package bg.fmi.controllers;

import bg.fmi.models.User;
import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.SystemConfigurationRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.response.ConfigResponse;
import bg.fmi.services.ConfigService;
import bg.fmi.services.UserService;
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
public class ConfigControllerTest {

    @Mock
    private ConfigService configService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConfigController configController;


    @Test
    public void testGetAll() {
        Set<ConfigResponse> projectConfigRespons = new HashSet<>();
        when(configService.getAll(any())).thenReturn(projectConfigRespons);
        when(userService.getUser()).thenReturn(new User());

        ResponseEntity<Set<ConfigResponse>> response = configController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectConfigRespons, response.getBody());
        verify(configService).getAll(any());
        verify(userService).getUser();
    }

    @Test
    public void testCreate() {
        SystemConfigurationRequest config = new SystemConfigurationRequest("Test SystemConfiguration", "Test Description");
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = configController.create(config);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).create(config, user);
        verify(userService).getUser();
    }

    @Test
    void testDeleteSuccess() {
        String configName = "testConfig";
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = configController.delete(configName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).delete(configName, user);
        verify(userService).getUser();
    }

    @Test
    void testAddUsersSuccess() {
        UsersRequest usersRequest = new UsersRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = configController.addUsers(usersRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).addUsers(usersRequest, user);
        verify(userService).getUser();
    }

    @Test
    void testRemoveUsersSuccess() {
        UsersRequest usersRequest = new UsersRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = configController.removeUsers(usersRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).removeUsers(usersRequest, user);
        verify(userService).getUser();
    }

    @Test
    void testGetConfigsSuccess() {
        String configName = "testConfig";
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<ConfigResponse> response = configController.getConfiguration(configName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).getConfiguration(configName, user);
        verify(userService).getUser();
    }

    @Test
    void testUpdateConfigsSuccess() {
        ConfigsRequest configsRequest = new ConfigsRequest();
        User user = new User();
        when(userService.getUser()).thenReturn(user);

        ResponseEntity<Void> response = configController.updateConfigs(configsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configService).updateConfigs(configsRequest, user);
        verify(userService).getUser();
    }
}