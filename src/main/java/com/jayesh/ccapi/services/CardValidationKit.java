package com.jayesh.ccapi.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CardValidationKit {

    public static int sumDigits(int[] arr) {
        return Arrays.stream(arr).sum();
    }

    public static boolean checkCardFormat(String cardNumber){
        cardNumber =  cardNumber.replaceAll("\\s", "");
        if(cardNumber.length() > 19){
            return false;
        }
        try{
            Long cardParsed = Long.parseLong(cardNumber);
        }
        catch (Exception e){
            return false;
        }

        if(!isValidCreditCardNumber(cardNumber)) {
            return false;
        }
        return true;
    }


    public static boolean isValidCreditCardNumber(String cardNumber) {
        // int array for processing the cardNumber
        int[] cardIntArray = new int[cardNumber.length()];

        for (int i = 0; i < cardNumber.length(); i++) {
            char c = cardNumber.charAt(i);
            cardIntArray[i] = Integer.parseInt("" + c);
        }
        for (int i = cardIntArray.length - 2; i >= 0; i = i - 2) {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if (num > 9) {
                num = num % 10 + num / 10;  // step 2
            }
            cardIntArray[i] = num;
        }

        int sum = sumDigits(cardIntArray);  // step 3

        if (sum % 10 == 0)  // step 4
        {
            return true;
        }
        return false; }

    public static boolean amountValidator(String Amount){
        Amount = Amount.replaceAll("\\s", "");
        if(Amount.length() < 2){
            return false;
        }

        char first = Amount.charAt(0);
        if(first != '$') {
            return false;
        }
        String num = Amount.substring(1);
        try{
            Double d = Double.parseDouble(num);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public static Double amount_from_String(String Amount){
        Amount = Amount.replaceAll("\\s", "");
        String num = Amount.substring(1);

        Double roundOff = Math.round(Double.parseDouble(num) * 100.0) / 100.0;
        return roundOff;

    }
}
