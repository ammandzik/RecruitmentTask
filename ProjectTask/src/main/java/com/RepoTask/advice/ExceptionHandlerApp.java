package com.RepoTask.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerApp {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIO(IOException ex) {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "500", "message", "Unexpected error occurred."));
    }
}
