package com.example.notes.controller;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> notFound(IllegalArgumentException ex) {
    return Map.of("error", "not_found", "message", ex.getMessage());
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void ignoreWhenDeletingMissing() {
    // 204 No Content, если удаляем несуществующий id
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(MethodArgumentNotValidException ex) {
    var msg =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
            .findFirst()
            .orElse("validation error");
    return Map.of("error", "bad_request", "message", msg);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> constraint(DataIntegrityViolationException ex) {
    return Map.of(
        "error", "constraint_violation", "message", ex.getMostSpecificCause().getMessage());
  }
}
