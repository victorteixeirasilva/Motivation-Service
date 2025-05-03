package tech.inovasoft.inevolving.ms.motivation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ExceptionResponse;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;

//@ControllerAdvice(basePackages = {"motivation.src.main.java.tech.inovasoft.inevolving.ms.motivation.controller"})
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaximumNumberOfRegisteredDreamsException.class)
    private ResponseEntity maximumNumberOfRegisteredDreamsException(MaximumNumberOfRegisteredDreamsException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        exception.getClass().getName(),
                        exception.getMessage()));
    }


}
