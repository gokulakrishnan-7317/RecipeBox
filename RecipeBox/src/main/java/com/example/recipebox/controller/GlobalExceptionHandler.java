package com.example.recipebox.controller;

import com.example.recipebox.exception.BusinessRuleException;
import com.example.recipebox.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException ex){ return body(HttpStatus.NOT_FOUND,ex.getMessage()); }
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> business(BusinessRuleException ex){ return body(HttpStatus.BAD_REQUEST,ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex){
        Map<String,Object> m=new LinkedHashMap<>(); m.put("timestamp",LocalDateTime.now()); m.put("status",400); m.put("error","Validation failed");
        Map<String,String> fields=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e->fields.put(e.getField(),e.getDefaultMessage())); m.put("fields",fields); return ResponseEntity.badRequest().body(m);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> generic(Exception ex){ return body(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error. Check the application console for details."); }
    private ResponseEntity<?> body(HttpStatus s,String msg){ Map<String,Object> m=new LinkedHashMap<>();m.put("timestamp",LocalDateTime.now());m.put("status",s.value());m.put("error",msg);return ResponseEntity.status(s).body(m); }
}
