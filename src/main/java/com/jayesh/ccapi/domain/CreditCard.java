package com.jayesh.ccapi.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;

import javax.persistence.Id;

import java.util.Objects;

@ApiModel(value = "Credit Card" ,description ="Credit Card Object")
@Entity
public class CreditCard {

    @Id
    @ApiModelProperty(notes = "Credit Card Number, Not more than 19 Digits satisfying Luhn Check",required = true,example = "4111 1111 1111 1111")
    private String creditCard;

    @ApiModelProperty(hidden = true)
    private String balance;

    @ApiModelProperty(notes = "Credit Card Limit, Must begin with $ followed by Limit Amount",required = true,example = "$35.40")
    private String cardlimit;

    public CreditCard(){}

    public CreditCard(String creditCard,String cardlimit) {

        this.creditCard = creditCard;
        this.balance = "$0.00";
        this.cardlimit = cardlimit;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public String getCardlimit() {
        return cardlimit;
    }

    public void setCardlimit(String cardlimit) {
        this.cardlimit = cardlimit;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "creditCard='" + creditCard + '\'' +
                ", balance='" + balance + '\'' +
                ", cardlimit='" + cardlimit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return creditCard.equals(that.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditCard);
    }
}
