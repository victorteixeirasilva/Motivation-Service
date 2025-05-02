package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class UserWithoutAuthorizationAboutThisDreamException extends Exception {
    public UserWithoutAuthorizationAboutThisDreamException() {
        super("Usuário não é o dono do sonho informado!");
    }
}
