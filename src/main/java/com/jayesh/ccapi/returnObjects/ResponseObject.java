package com.jayesh.ccapi.returnObjects;

import com.jayesh.ccapi.domain.CreditCard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@ApiModel(value = "Response Object" ,description = "Request Response Object")
public class ResponseObject {


    @ApiModelProperty(notes = "Message",required = true,example = "Request Accepted, Card Created",position = 3)
    private String message;

    @ApiModelProperty(notes = "Response Object, Specifies the Credit Card Object",required = true,position = 4)
    private Object object;

    public ResponseObject(String message, Object object) {

        this.message = message;
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
