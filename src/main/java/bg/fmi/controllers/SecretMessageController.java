package bg.fmi.controllers;

import bg.fmi.payload.request.SecretMessageRequest;
import bg.fmi.payload.response.SecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessagesCountResponse;
import bg.fmi.services.SecretMessageService;
import bg.fmi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/secret-message")
public class SecretMessageController {

    @Autowired
    private SecretMessageService secretMessageService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody SecretMessageRequest secretMessageRequest) {
        secretMessageService.create(secretMessageRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SecretMessageResponse> getSecret(@NotBlank @Param("uuid") String uuid) {

        return ResponseEntity.ok(secretMessageService.getSecret(uuid, userService.getUser()));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UnreadSecretMessageResponse>> getUnreadSecrets() {

        return ResponseEntity.ok(secretMessageService.getUnreadSecrets(userService.getUser()));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UnreadSecretMessagesCountResponse> getUnreadSecretsCount() {

        return ResponseEntity.ok(secretMessageService.getUnreadSecretsCount(userService.getUser()));
    }
}
