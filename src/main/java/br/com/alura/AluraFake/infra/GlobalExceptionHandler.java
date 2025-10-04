package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorItemDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorItemDTO error = new ErrorItemDTO("businessRule", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorItemDTO> handleIllegalState(IllegalStateException ex) {
        ErrorItemDTO error = new ErrorItemDTO("businessRule", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}