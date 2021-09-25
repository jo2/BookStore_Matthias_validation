package de.adesso.bookStore;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ControllerAdviser {

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity handleException() {
        return ResponseEntity.badRequest().build();
    }
}
