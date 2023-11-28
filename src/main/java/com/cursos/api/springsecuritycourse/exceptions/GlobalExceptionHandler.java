package com.cursos.api.springsecuritycourse.exceptions;

import com.cursos.api.springsecuritycourse.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice //Mapean errores
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class) // manejan los errores
    public ResponseEntity<?> handlerGenericException(Exception exception, HttpServletRequest request){

        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setMessage("Error interno en el servidor, vuelva a intentarlo");
        apiError.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // manejan los errores
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                   HttpServletRequest request){

        ApiError apiError = new ApiError();
        apiError.setBackendMessage(exception.getLocalizedMessage());
        apiError.setUrl(request.getRequestURL().toString());
        apiError.setMethod(request.getMethod());
        apiError.setMessage("Error en la peteción enviada, vuelva a intentarlo");
        apiError.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}
