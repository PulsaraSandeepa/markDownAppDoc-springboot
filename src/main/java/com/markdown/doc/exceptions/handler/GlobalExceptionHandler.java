package com.markdown.doc.exceptions.handler;

import com.markdown.doc.exceptions.UserNotAllowedException;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //userNotAllowedException
    @ExceptionHandler({UserNotAllowedException.class})
    public ResponseEntity<Object> handleUserNotAllowedException(final UserNotAllowedException ex, final WebRequest request) {
        String bodyResponseString = "You are not allowed to execute this action";
        log.error(bodyResponseString);
        return handleExceptionInternal(ex, bodyResponseString, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    //no such element exception
    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(final NoSuchElementException ex, final WebRequest request) {
        String bodyResponseString = "You are not allowed to execute this action";
        log.error(bodyResponseString);
        return handleExceptionInternal(ex, bodyResponseString, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

}
