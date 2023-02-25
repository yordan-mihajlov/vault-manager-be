package bg.fmi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import bg.fmi.exceptions.AccessDeniedException;
import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.exceptions.ResourceAlreadyExists;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;

    @Mock
    private WebRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controllerAdvisor = new ControllerAdvisor();
    }

    @Test
    public void handleNoDataFoundExceptionTest() {
        NoDataFoundException exception = new NoDataFoundException();
        when(request.getDescription(false)).thenReturn("Test request");
        ResponseEntity<Map> response = controllerAdvisor.handleNoDataFoundException(exception, request);
        Map<String, Object> body = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No data found", body.get("message"));
        assertEquals(LocalDateTime.class, body.get("timestamp").getClass());
    }

    @Test
    public void handleAccessDeniedExceptionTest() {
        AccessDeniedException exception = new AccessDeniedException();
        when(request.getDescription(false)).thenReturn("Test request");
        ResponseEntity<Map> response = controllerAdvisor.handleAccessDeniedException(exception, request);
        Map<String, Object> body = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User does not have rights for this operation", body.get("message"));
        assertEquals(LocalDateTime.class, body.get("timestamp").getClass());
    }

    @Test
    public void handleResourceAlreadyExistsTest() {
        ResourceAlreadyExists exception = new ResourceAlreadyExists();
        when(request.getDescription(false)).thenReturn("Test request");
        ResponseEntity<Map> response = controllerAdvisor.handleResourceAlreadyExists(exception, request);
        Map<String, Object> body = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("There is already such kind of resource", body.get("message"));
        assertEquals(LocalDateTime.class, body.get("timestamp").getClass());
    }
}

