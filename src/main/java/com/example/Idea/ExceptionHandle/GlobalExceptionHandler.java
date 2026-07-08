package com.example.Idea.ExceptionHandle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(InvalidLoanStatusException.class)
    public ResponseEntity<Map<String, String>> handleinvalidTransactiion(
            InvalidLoanStatusException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficient(
            InsufficientBalanceException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(InvalidAccountNumberException.class)
    public ResponseEntity<Map<String,String>>handlerinvalidAcc(InvalidAccountNumberException ex){
        Map<String,String>error=new HashMap<>();
        error.put("message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> error = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(error);
    }
}
