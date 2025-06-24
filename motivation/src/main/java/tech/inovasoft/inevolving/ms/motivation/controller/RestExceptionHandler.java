package tech.inovasoft.inevolving.ms.motivation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaximumNumberOfRegisteredDreamsException.class)
    private ResponseEntity<ExceptionResponse> maximumNumberOfRegisteredDreamsException(MaximumNumberOfRegisteredDreamsException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()));
    }

    @ExceptionHandler(NotSavedDTOInDbException.class)
    private ResponseEntity<ExceptionResponse> notSavedDTOInDbException(NotSavedDTOInDbException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(UserWithoutAuthorizationAboutThisDreamException.class)
    private ResponseEntity<ExceptionResponse> userWithoutAuthorizationAboutThisDreamException(UserWithoutAuthorizationAboutThisDreamException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(DreamNotFoundException.class)
    private ResponseEntity<ExceptionResponse> dreamNotFoundException(DreamNotFoundException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


    @ExceptionHandler(DataBaseException.class)
    private ResponseEntity<ExceptionResponse> dataBaseException(DataBaseException exception) {
        log.error("ERROR: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        exception.getClass().getSimpleName(),
                        exception.getMessage()
                ));
    }


}
