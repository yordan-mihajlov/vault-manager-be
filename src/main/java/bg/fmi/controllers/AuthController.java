package bg.fmi.controllers;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import bg.fmi.payload.request.SignupSystemRequest;
import bg.fmi.payload.response.RegisterResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bg.fmi.models.ERole;
import bg.fmi.models.Role;
import bg.fmi.models.User;
import bg.fmi.payload.request.LoginRequest;
import bg.fmi.payload.request.SignupRequest;
import bg.fmi.repositories.RoleRepository;
import bg.fmi.repositories.UserRepository;
import bg.fmi.security.jwt.JwtUtils;
import bg.fmi.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static Logger log = Logger.getLogger(AuthController.class.getName());

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<RegisterResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains("ROLE_SYSTEM")) {
            return ResponseEntity.badRequest().build();
        }

        log.info(String.format("User %s has been successfully authenticated", getUsername()));

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new RegisterResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        null,
                        null,
                        null,
                        roles));
    }

    @PostMapping("/get-token")
    public ResponseEntity<String> getToken(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtUtils.generateJwt(userDetails);

        log.info(String.format("User %s successfully fetch JWT", getUsername()));

        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName());


            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(signUpRequest.getUsername());

        log.info(String.format("User %s has been successfully registered", signUpRequest.getUsername()));

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(new RegisterResponse(user.getId(),
                    user.getUsername(),
                    null, null, null,
                   user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList())));
  }

    @PostMapping("/signup-system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> registerSystem(@Valid @RequestBody SignupSystemRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        String id = UUID.randomUUID().toString();
        // Create new system's account
        User user = new User(signUpRequest.getUsername(),
                id + "@vault-manager.com",
                encoder.encode(signUpRequest.getPassword()),
                id,
                id);


        Role systemRole = roleRepository.findByName(ERole.ROLE_SYSTEM)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRoles(Set.of(systemRole));
        userRepository.save(user);

        log.info(String.format("System %s has been successfully registered", signUpRequest.getUsername()));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signout")
    public ResponseEntity<RegisterResponse> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        log.info(String.format("User %s has been successfully logged out", getUsername()));

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new RegisterResponse());
    }

    private String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return authentication.getName();
    }
}
