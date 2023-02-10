package bg.fmi.controllers;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import bg.fmi.payload.request.SecretMessageRequest;
import bg.fmi.payload.response.SecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessageResponse;
import bg.fmi.services.SecretMessageService;
import bg.fmi.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SecretMessageControllerTest {

    @InjectMocks
    private SecretMessageController secretMessageController;

    @Mock
    private SecretMessageService secretMessageService;

    @Mock
    private UserService userService;

    @Test
    void testCreate() {
        SecretMessageRequest secretMessageRequest = new SecretMessageRequest();
        when(secretMessageService.create(secretMessageRequest)).thenReturn("12345");

        ResponseEntity<String> response = secretMessageController.create(secretMessageRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("12345");
    }

    @Test
    void testGetSecret() {
        SecretMessageResponse secretMessageResponse = new SecretMessageResponse();
        when(secretMessageService.getSecret("12345", userService.getUser())).thenReturn(secretMessageResponse);

        ResponseEntity<SecretMessageResponse> response = secretMessageController.getSecret("12345");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(secretMessageResponse);
    }

    @Test
    void testGetUnreadSecrets() {
        List<UnreadSecretMessageResponse> unreadSecrets = new ArrayList<>();
        when(secretMessageService.getUnreadSecrets(userService.getUser())).thenReturn(unreadSecrets);

        ResponseEntity<List<UnreadSecretMessageResponse>> response = secretMessageController.getUnreadSecrets();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(unreadSecrets);
    }
}