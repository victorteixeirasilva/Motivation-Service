package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class DreamNotFoundException extends Exception{
    public DreamNotFoundException() {
        super("The reported dream could not be located.");
    }

    public DreamNotFoundException(String message) {
        super(message);
    }
}
