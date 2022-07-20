package bg.fmi.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("User does not have rights for this operation");
    }
}