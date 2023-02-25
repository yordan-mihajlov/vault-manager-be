package bg.fmi.controllers;

import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.request.ProjectRequest;
import bg.fmi.payload.response.ProjectResponse;
import bg.fmi.services.ProjectService;
import bg.fmi.services.UserService;
import bg.fmi.vaultmanagerclient.component.VaultManagerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private VaultManagerProvider vaultManagerProvider;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Set<ProjectResponse>> getAll() {

        return ResponseEntity.ok(projectService.getAll(userService.getUser()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody ProjectRequest project) {

        projectService.create(project, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@Valid @RequestBody String projectName) {

        projectService.delete(projectName, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addUsers(@Valid @RequestBody UsersRequest usersRequest) {

        projectService.addUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUsers(@Valid @RequestBody UsersRequest usersRequest) {

        projectService.changeUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUsers(@Valid @RequestBody UsersRequest usersRequest) {

        projectService.removeUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-configs")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ProjectResponse> getConfigs(@NotBlank @Param("projectName") String projectName) {

        return ResponseEntity.ok(projectService.getConfiguration(projectName, userService.getUser()));
    }

    @GetMapping("/get-all-configs")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getAllConfigs() {
        List<ProjectResponse> projectResponses = new ArrayList<>();

        vaultManagerProvider.getProperties().forEach((s, stringStringMap) -> {
                    projectResponses.add(ProjectResponse.builder().name(s).configurations(stringStringMap).build());
                });
        return ResponseEntity.ok(projectResponses);
    }

    @PostMapping("/update-configs")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateConfigs(@Valid @RequestBody ConfigsRequest configsRequest) {

        projectService.updateConfigs(configsRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }
}
