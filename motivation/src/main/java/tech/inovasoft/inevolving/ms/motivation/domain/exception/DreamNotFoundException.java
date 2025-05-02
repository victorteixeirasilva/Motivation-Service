package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class DreamNotFoundException extends RuntimeException{
    public DreamNotFoundException() {
        super("Não foi possível localizar o sonho informado.");
    }
}
