package bg.fmi.controllers;

import bg.fmi.payload.request.ConfigsRequest;
import bg.fmi.payload.request.UsersRequest;
import bg.fmi.payload.request.SystemConfigurationRequest;
import bg.fmi.payload.response.ConfigResponse;
import bg.fmi.services.ConfigService;
import bg.fmi.services.FileService;
import bg.fmi.services.UserService;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Set<ConfigResponse>> getAll() {

        return ResponseEntity.ok(configService.getAll(userService.getUser()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody SystemConfigurationRequest config) {

        configService.create(config, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@Valid @RequestBody String configName) {

        configService.delete(configName, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addUsers(@Valid @RequestBody UsersRequest usersRequest) {

        configService.addUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUsers(@Valid @RequestBody UsersRequest usersRequest) {

        configService.changeUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-systems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeSystems(@Valid @RequestBody UsersRequest usersRequest) {

        configService.changeSystems(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUsers(@Valid @RequestBody UsersRequest usersRequest) {

        configService.removeUsers(usersRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-data")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ConfigResponse> getConfiguration(@NotBlank @Param("configName") String configName) {

        return ResponseEntity.ok(configService.getConfiguration(configName, userService.getUser()));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateConfigs(@Valid @RequestBody ConfigsRequest configsRequest) {

        configService.updateConfigs(configsRequest, userService.getUser());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> exportConfigs(@NotBlank @Param("configNames") String[] configNames) throws IOException {

        List<ConfigResponse> configResponses = new ArrayList<>();

        Arrays.stream(configNames).forEach(configName -> {
            configResponses.add(configService.getConfiguration(configName, userService.getUser()));
        });

        byte[] bytes = fileService.exportCSV(configResponses);
        Resource resource = new ByteArrayResource(bytes);

        // Set headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.csv");

        // Return ResponseEntity with Resource and headers
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> importConfigs(@RequestParam("file") MultipartFile file) throws IOException, CsvException {
        List<ConfigResponse> configResponses = fileService.importCSV(file);
        configResponses.forEach(configResponse -> {
            configService.create(SystemConfigurationRequest.builder()
                            .name(configResponse.getName())
                            .description(configResponse.getDescription())
                    .build(), userService.getUser());
            configService.updateConfigs(ConfigsRequest.builder()
                            .configName(configResponse.getName())
                            .configs(configResponse.getConfigurations())
                    .build(), userService.getUser());
            configService.changeUsers(UsersRequest.builder()
                            .configName(configResponse.getName())
                            .usernames(configResponse.getUsernames())
                    .build(), userService.getUser());
            configService.changeSystems(UsersRequest.builder()
                    .configName(configResponse.getName())
                    .usernames(configResponse.getSystemnames())
                    .build(), userService.getUser());
        });
        return ResponseEntity.ok().build();
    }
}
