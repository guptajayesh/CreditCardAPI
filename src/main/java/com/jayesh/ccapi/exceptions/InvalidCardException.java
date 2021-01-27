package com.jayesh.ccapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,reason = "Invalid Credit Card in Request")
public class InvalidCardException extends RuntimeException{

    public InvalidCardException(String Card){
        super("Credit Card Number is Invalid: " + Card);
    }
}
