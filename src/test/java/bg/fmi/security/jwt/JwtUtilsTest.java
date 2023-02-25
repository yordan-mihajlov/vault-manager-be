package bg.fmi.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import bg.fmi.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseCookie;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        jwtUtils = new JwtUtils();
        jwtUtils.jwtSecret = "secret";
        jwtUtils.jwtExpirationMs = 360000;
        jwtUtils.jwtCookie = "JWT-COOKIE";
    }

    @Test
    void getJwtFromCookiesTest() {
        Cookie cookie = new Cookie("JWT-COOKIE", "JWT_TOKEN");
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });
        assertThat(jwtUtils.getJwtFromCookies(request)).isEqualTo("JWT_TOKEN");
    }

    @Test
    void generateJwtCookieTest() {
        ResponseCookie cookie = jwtUtils.generateJwtCookie("username");
        assertThat(cookie.getName()).isEqualTo("JWT-COOKIE");
        assertThat(cookie.getValue()).isNotEmpty();
        assertThat(cookie.getPath()).isEqualTo("/api");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(24 * 60 * 60);
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void generateJwtTest() {
        UserDetailsImpl userPrincipal = new UserDetailsImpl(1L, "username", "email", "password", null);
        String jwt = jwtUtils.generateJwt(userPrincipal);
        assertThat(jwt).isNotEmpty();
    }

    /*@Test
    void getCleanJwtCookieTest() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        assertThat(cookie.getName()).isEqualTo("JWT-COOKIE");
        assertThat(cookie.getValue()).isEqualTo("");
        assertThat(cookie.getPath()).isEqualTo("/api");
    }*/

    @Test
    void getUserNameFromJwtTokenTest() {
        String token = jwtUtils.generateTokenFromUsername("username");
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("username");
    }

    @Test
    public void testValidateJwtToken_validToken() {
        String validToken = Jwts.builder()
                .setSubject("username")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000))
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
        boolean result = jwtUtils.validateJwtToken(validToken);
        assertTrue(result);
    }

    @Test
    public void testValidateJwtToken_invalidToken() {
        String invalidToken = "invalid-token";
        boolean result = jwtUtils.validateJwtToken(invalidToken);
        assertFalse(result);
    }

    @Test
    public void testValidateJwtToken_expiredToken() {
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3NDMyMjA0NywiZXhwIjoxNjc0NDA4NDQ" +
                "3fQ.GQ287m3iUop0CSQcuUDOhyuq12kT9Yc_ALUWpwNJzYgb7yRqU0ssNpn5_jZx2A1UENwE06X82-Y9V7OjS15aqw";
        jwtUtils.jwtSecret = "vaultmanagerSecretKey";
        boolean result = jwtUtils.validateJwtToken(expiredToken);
        assertFalse(result);
    }

}