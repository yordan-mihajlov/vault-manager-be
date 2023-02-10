package bg.fmi.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import bg.fmi.models.ERole;
import bg.fmi.models.Role;
import bg.fmi.models.User;
import bg.fmi.payload.request.SignupRequest;
import bg.fmi.repository.RoleRepository;
import bg.fmi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import bg.fmi.payload.request.LoginRequest;
import bg.fmi.payload.response.UserInfoResponse;
import bg.fmi.security.jwt.JwtUtils;
import bg.fmi.security.services.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Test
    public void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest("testuser", "testpassword");
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getEmail()).thenReturn("testuser@test.com");
        when(userDetails.getAuthorities()).thenReturn(new HashSet<>());
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "testtoken").build();
        when(jwtUtils.generateJwtCookie("testuser")).thenReturn(jwtCookie);

        ResponseEntity<UserInfoResponse> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("testuser@test.com", response.getBody().getEmail());
        assertEquals(0, response.getBody().getRoles().size());
        HttpHeaders headers = response.getHeaders();
        assertEquals(1, headers.get(HttpHeaders.SET_COOKIE).size());
        assertEquals(jwtCookie.toString(), headers.get(HttpHeaders.SET_COOKIE).get(0));
    }

    @Test
    void getTokenValidCredentialsReturns200WithToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new TestingAuthenticationToken(null, null));
        when(jwtUtils.generateJwt(any())).thenReturn("valid_token");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user1");
        loginRequest.setPassword("password");

        ResponseEntity<String> response = authController.getToken(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("valid_token", response.getBody());
    }

    @Test
    void testRegisterUserUserExistsByUsername() {
        SignupRequest signupRequest;
        User user;
        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("testpassword");
        user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());

        when(userRepository.existsByUsername(signupRequest.getUsername())).thenReturn(true);
        ResponseEntity<UserInfoResponse> response = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserUserExistsByEmail() {
        SignupRequest signupRequest;
        User user;
        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("testpassword");
        user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
        ResponseEntity<UserInfoResponse> response = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRegisterUserUserDoesNotExist() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("testuser@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRole(new HashSet<>(Collections.singleton("ROLE_USER")));

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));
        when(encoder.encode("password")).thenReturn("hashedpassword");

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "testuser").build();
        when(jwtUtils.generateJwtCookie("testuser")).thenReturn(jwtCookie);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("hashedpassword");
        user.setRoles(Collections.singleton(new Role(1, ERole.ROLE_USER)));

        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<UserInfoResponse> response = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("testuser@example.com", response.getBody().getEmail());
        assertEquals(Collections.singletonList("ROLE_USER"), response.getBody().getRoles());
    }

    @Test
    public void testLogoutUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "testuser").build();
        when(jwtUtils.getCleanJwtCookie()).thenReturn(jwtCookie);

        ResponseEntity<Void> response = authController.logoutUser();

        assertEquals(200, response.getStatusCodeValue());
    }
}
