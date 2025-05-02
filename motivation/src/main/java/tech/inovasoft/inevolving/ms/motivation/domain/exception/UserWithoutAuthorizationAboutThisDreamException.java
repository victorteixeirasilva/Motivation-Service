package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class UserWithoutAuthorizationAboutThisDreamException extends RuntimeException {
    public UserWithoutAuthorizationAboutThisDreamException() {
        super("Usuário não é o dono do sonho informado!");
    }
}
