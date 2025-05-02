package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class MaximumNumberOfRegisteredDreamsException extends RuntimeException{
    public MaximumNumberOfRegisteredDreamsException() {
        super("Não foi possível cadastrar o sonho pois o mesmo já tem 200 sonhos cadastrados.");
    }
}
