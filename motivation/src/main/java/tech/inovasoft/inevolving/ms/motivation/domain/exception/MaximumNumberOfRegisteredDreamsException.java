package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class MaximumNumberOfRegisteredDreamsException extends Exception{
    public MaximumNumberOfRegisteredDreamsException() {
        super("It was not possible to register the dream because it already has 200 registered dreams.");
    }
}
