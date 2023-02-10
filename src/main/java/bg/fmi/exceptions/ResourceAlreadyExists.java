package bg.fmi.exceptions;

public class ResourceAlreadyExists extends RuntimeException {
    public ResourceAlreadyExists() {
        super("There is already such kind of resource");
    }
}