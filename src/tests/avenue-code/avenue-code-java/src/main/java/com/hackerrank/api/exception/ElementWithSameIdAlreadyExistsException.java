package com.hackerrank.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ElementWithSameIdAlreadyExistsException extends RuntimeException {
    
    public ElementWithSameIdAlreadyExistsException(String message) {
        super(message);
    }
}
