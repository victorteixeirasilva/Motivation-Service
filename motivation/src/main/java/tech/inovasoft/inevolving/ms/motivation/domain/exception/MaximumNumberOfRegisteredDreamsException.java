package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class MaximumNumberOfRegisteredDreamsException extends Exception{
    public MaximumNumberOfRegisteredDreamsException() {
        super("Não foi possível cadastrar o sonho pois o mesmo já tem 200 sonhos cadastrados.");
    }
}
