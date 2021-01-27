package com.jayesh.ccapi.returnObjects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

@ApiModel(value = "Credit Card Payment" ,description ="Credit Card Pay Object")
public class payObject {

    @ApiModelProperty(notes = "Credit Card Number, Not more than 19 digits, Luhn Validated",required = true,example = "4111 1111 1111 1111")
    private String creditCard;
    @ApiModelProperty(notes = "Credit Card Payment, Must begin with $ followed by Pay Amount",required = true,example = "$35.40")
    private String pay;

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }
}
