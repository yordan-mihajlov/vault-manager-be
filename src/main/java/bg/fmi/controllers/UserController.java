package bg.fmi.controllers;

import bg.fmi.models.ERole;
import bg.fmi.models.User;
import bg.fmi.payload.request.MarkUsersAsAdminsRequest;
import bg.fmi.payload.response.RegisterResponse;
import bg.fmi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/mark-users-as-admins")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> markUsersAsAdmins(@Valid @RequestBody MarkUsersAsAdminsRequest markUsersAsAdminsRequest) {
        userService.markUsersAsAdmins(markUsersAsAdminsRequest.getUsernames());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/usernames")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<String>> getUsernames() {
        List<String> usernames = userService.getUsernames();

        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/usernames-by-role")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getUsernamesByRoles(@NotBlank @Param("roles") ERole role) {
        List<String> usernames = userService.getUsernames(role);

        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/user-details")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> getUserDetails() {
        User user = userService.getUser();

        return ResponseEntity.ok(userService.getUserDetails());
    }
}
