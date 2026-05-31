package com.glauber.voting.infrastructure.exception;

import com.glauber.voting.application.exception.SessionException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Captura as exceções dos módulos application domain
    @ExceptionHandler(SessionException.class)
    public ResponseEntity<Map<String, Object>> handleSessionException(SessionException ex) {
        
        String friendlyMessage = messageSource.getMessage(
                ex.getErrorCode(),
                ex.getArgs(),
                ex.getMessage(),
                LocaleContextHolder.getLocale()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getErrorCode());
        body.put("message", friendlyMessage);
        body.put("status", HttpStatus.BAD_REQUEST.value());

        HttpStatus status = ex.getErrorCode().contains("not_found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(body);
    }
    
    @ExceptionHandler(CpfValidatorException.class)
    public ResponseEntity<Map<String, String>> handleCpfException(CpfValidatorException ex) {
        return ResponseEntity.badRequest().body(Map.of("warning", ex.getMessage()));
    }

    // Captura dos erros de Webflux
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleWebFluxValidationException(WebExchangeBindException ex) {

        String friendlyMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro de validação nos campos informados (WebFlux).");

        return buildValidationResponse(friendlyMessage);
    }

    // Captura dos erros do MVC
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMvcValidationException(MethodArgumentNotValidException ex) {

        String friendlyMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erro de validação nos campos informados (MVC).");

        return buildValidationResponse(friendlyMessage);
    }

    private ResponseEntity<Map<String, Object>> buildValidationResponse(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "argument.not_valid");
        body.put("message", message);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }
}