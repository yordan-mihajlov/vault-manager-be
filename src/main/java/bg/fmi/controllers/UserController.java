package bg.fmi.controllers;

import bg.fmi.models.ERole;
import bg.fmi.models.User;
import bg.fmi.payload.response.UserInfoResponse;
import bg.fmi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@PreAuthorize("hasRole('MODERATOR')")
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/mark-users-as-admins")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> markUsersAsAdmins(@NotBlank @Param("username") List<String> usernames) {
        userService.markUsersAsAdmins(usernames);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/usernames")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<String>> getUsernames() {
        List<String> usernames = userService.getUsernames();

        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/usernames-by-roles")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getUsernamesByRoles(@NotBlank @Param("roles") ERole[] roles) {
        List<String> usernames = userService.getUsernames(Set.of(roles));

        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/user-details")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> getUserDetails() {
        User user = userService.getUser();

        return ResponseEntity.ok(userService.getUserDetails());
    }
}
