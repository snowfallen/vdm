package vdm.shop.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSqlIntegrityConstraintViolationException(
            SQLIntegrityConstraintViolationException ex) {
        String originalMessage = ex.getMessage();
        String customMessage = originalMessage;

        if (originalMessage != null && originalMessage.contains("Duplicate entry")) {
            int firstQuote = originalMessage.indexOf('\'');
            int secondQuote = originalMessage.indexOf('\'', firstQuote + 1);
            if (firstQuote != -1 && secondQuote != -1) {
                customMessage = originalMessage.substring(0, secondQuote + 1);
            }
        }
        return new ResponseEntity<>(customMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePsqlException(PSQLException ex) {
        String originalMessage = ex.getMessage();
        String customMessage = originalMessage;

        if (originalMessage != null
                && originalMessage.contains("duplicate key value violates unique constraint")) {
            int firstQuote = originalMessage.indexOf('(');
            int secondQuote = originalMessage.indexOf(')', firstQuote + 1);
            int thirdQuote = originalMessage.indexOf('(', secondQuote + 1);
            int foursQuote = originalMessage.indexOf(')', thirdQuote + 1);
            if (firstQuote != -1 && secondQuote != -1) {
                customMessage = "Duplicate entry: '"
                        + originalMessage.substring(firstQuote + 1, secondQuote) + "'"
                        + originalMessage.substring(thirdQuote + 1, foursQuote) + "'";
            }
        }

        return new ResponseEntity<>(customMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<List<String>> handleRegistrationException(
            RegistrationException ex) {
        return new ResponseEntity<>(List.of(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<List<String>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(List.of(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = error.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }
}
