package bg.fmi.controllers;

import bg.fmi.payload.request.SecretMessageRequest;
import bg.fmi.payload.response.SecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessageResponse;
import bg.fmi.services.SecretMessageService;
import bg.fmi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/secret-message")
public class SecretMessageController {

    @Autowired
    private SecretMessageService secretMessageService;

    @Autowired
    private UserService userService;

    @PostMapping("/create-secret")
    public ResponseEntity<String> create(@Valid @RequestBody SecretMessageRequest secretMessageRequest) {

        return ResponseEntity.ok(secretMessageService.create(secretMessageRequest));
    }

    @GetMapping("/get-secret")
    public ResponseEntity<SecretMessageResponse> getSecret(@NotBlank @Param("uuid") String uuid) {

        return ResponseEntity.ok(secretMessageService.getSecret(uuid, userService.getUser()));
    }

    @GetMapping("/unread-secrets")
    public ResponseEntity<List<UnreadSecretMessageResponse>> getUnreadSecrets() {

        return ResponseEntity.ok(secretMessageService.getUnreadSecrets(userService.getUser()));
    }
}
