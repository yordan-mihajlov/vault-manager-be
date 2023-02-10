package bg.fmi.security.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.fmi.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter filter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void doFilterInternalValidJwtSetsAuthentication() throws ServletException, IOException {
        when(jwtUtils.validateJwtToken(any())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(any())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtUtils.getJwtFromCookies(any())).thenReturn("valid_jwt");

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername("username");
    }

    @Test
    public void doFilterInternalInvalidJwtDoesNotSetAuthentication() throws ServletException, IOException {
        when(jwtUtils.validateJwtToken(any())).thenReturn(false);
        when(jwtUtils.getJwtFromCookies(any())).thenReturn("invalid_jwt");

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserByUsername("username");
    }
}
