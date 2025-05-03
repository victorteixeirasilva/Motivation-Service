package tech.inovasoft.inevolving.ms.motivation.domain.exception;

public class UserWithoutAuthorizationAboutThisDreamException extends Exception {
    public UserWithoutAuthorizationAboutThisDreamException() {
        super("User is not the owner of the reported dream!");
    }
}
