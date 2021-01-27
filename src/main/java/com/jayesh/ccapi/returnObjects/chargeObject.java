package com.jayesh.ccapi.returnObjects;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Credit Card Charge" ,description ="Credit Card Charge Object")
public class chargeObject {

    @ApiModelProperty(notes = "Credit Card Number, Not more than 19 digits, Luhn Validated",required = true,example = "4111 1111 1111 1111")
    private String creditCard;

    @ApiModelProperty(notes = "Credit Card Charge, Must begin with $ followed by Charge Amount",required = true,example = "$35.40")
    private String charge;

    public chargeObject(String creditCard, String charge) {
        this.creditCard = creditCard;
        this.charge = charge;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }
}
