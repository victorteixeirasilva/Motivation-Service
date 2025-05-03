package tech.inovasoft.inevolving.ms.motivation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaximumNumberOfRegisteredDreamsException.class)
    private ResponseEntity maximumNumberOfRegisteredDreamsException(MaximumNumberOfRegisteredDreamsException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()));
    }

    @ExceptionHandler(NotSavedDTOInDbException.class)
    private ResponseEntity notSavedDTOInDbException(NotSavedDTOInDbException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(UserWithoutAuthorizationAboutThisDreamException.class)
    private ResponseEntity userWithoutAuthorizationAboutThisDreamException(UserWithoutAuthorizationAboutThisDreamException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(DreamNotFoundException.class)
    private ResponseEntity dreamNotFoundException(DreamNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(DataBaseException.class)
    private ResponseEntity dataBaseException(DataBaseException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
