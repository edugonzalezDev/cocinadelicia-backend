package com.cocinadelicia.backend.common.web;

import com.cocinadelicia.backend.user.service.UserService.EmailConflictException;
import com.cocinadelicia.backend.user.service.UserService.MissingEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleEmailConflict(EmailConflictException ex) {
        return Map.of("error", "conflict", "message", ex.getMessage());
    }

    @ExceptionHandler(MissingEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMissingEmail(MissingEmailException ex) {
        return Map.of("error", "bad_request", "message", ex.getMessage());
    }
}
