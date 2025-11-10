// src/main/java/com/cocinadelicia/backend/common/web/GlobalExceptionHandler.java
package com.cocinadelicia.backend.common.web;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.user.service.UserService.EmailConflictException;
import com.cocinadelicia.backend.user.service.UserService.MissingEmailException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 1) Conflicto de email
  @ExceptionHandler(EmailConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiError handleEmailConflict(EmailConflictException ex, WebRequest request) {
    log.warn("Email conflict: {}", ex.getMessage());
    return ApiError.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), getPath(request));
  }

  // 2) Falta de email
  @ExceptionHandler(MissingEmailException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleMissingEmail(MissingEmailException ex, WebRequest request) {
    log.info("Missing email: {}", ex.getMessage());
    return ApiError.of(
        HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), getPath(request));
  }

  // 3) Validaciones @Valid / @NotNull / @Size ...
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (var error : ex.getBindingResult().getAllErrors()) {
      String fieldName = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
      String message = error.getDefaultMessage();
      fieldErrors.put(fieldName, message);
    }

    log.info("Validation error on {}: {}", getPath(request), fieldErrors);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", java.time.Instant.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", "Validation failed");
    body.put("path", getPath(request));
    body.put("fields", fieldErrors);
    return body;
  }

  // 4) 404 de controlador (cuando no encuentra endpoint, si está habilitado)
  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFound(NoHandlerFoundException ex, WebRequest request) {
    log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
    return ApiError.of(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        "El recurso solicitado no existe",
        getPath(request));
  }

  // 3.1) BadRequest de negocio — con code
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleBadRequest(BadRequestException ex, WebRequest request) {
    log.info("BadRequest: {}", ex.getMessage());
    return ApiError.of(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage(),
        getPath(request),
        ex.code());
  }

  // 3.2) NotFound de negocio — con code
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleNotFoundBusiness(NotFoundException ex, WebRequest request) {
    log.warn("NotFound: {}", ex.getMessage());
    return ApiError.of(
        HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), getPath(request), ex.code());
  }

  // ✅ 3.3) Access Denied (sin permisos) → 403
  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiError handleAccessDenied(Exception ex, WebRequest request) {
    log.warn("AccessDenied on {}: {}", getPath(request), ex.getMessage());
    return ApiError.of(
        HttpStatus.FORBIDDEN.value(),
        "Forbidden",
        "No tiene permisos para realizar esta acción.",
        getPath(request),
        "ACCESS_DENIED");
  }

  // 5) Fallback genérico (último recurso) → 500
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, WebRequest request) {
    log.error("Unexpected error on {}: {}", getPath(request), ex.getMessage(), ex);
    ApiError body =
        ApiError.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Ocurrió un error inesperado. Si persiste, contacte al administrador.",
            getPath(request));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private String getPath(WebRequest request) {
    String desc = request.getDescription(false); // "uri=/api/pedidos/1"
    return desc != null && desc.startsWith("uri=") ? desc.substring(4) : desc;
  }
}
