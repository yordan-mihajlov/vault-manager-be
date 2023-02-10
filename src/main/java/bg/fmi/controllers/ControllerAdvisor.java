package bg.fmi.controllers;

import bg.fmi.exceptions.AccessDeniedException;
import bg.fmi.exceptions.NoDataFoundException;
import bg.fmi.exceptions.ResourceAlreadyExists;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private static Logger log = Logger.getLogger(ControllerAdvisor.class.getName());

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map> handleNoDataFoundException(
            NoDataFoundException ex, WebRequest request) {

        log.error("No data found", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "No data found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.error("User does not have rights for this operation", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "User does not have rights for this operation");

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<Map> handleResourceAlreadyExists(
            ResourceAlreadyExists ex, WebRequest request) {

        log.error("There is already such kind of resource", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "There is already such kind of resource");

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}