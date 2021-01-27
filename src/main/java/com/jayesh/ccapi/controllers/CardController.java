package com.jayesh.ccapi.controllers;

import com.jayesh.ccapi.domain.CreditCard;
import com.jayesh.ccapi.repositories.CreditCardRepository;
import com.jayesh.ccapi.returnObjects.ResponseObject;
import com.jayesh.ccapi.returnObjects.chargeObject;
import com.jayesh.ccapi.returnObjects.payObject;
import com.jayesh.ccapi.services.CardValidationKit;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/card")
@Api(value="Credit Card", description = "API for Credit Card Operations")
public class CardController {

    @Autowired
    CardValidationKit cardValidationKit;

    @Autowired
    CreditCardRepository creditCardRepository;

    //Credit Card Get all Cards in Database
    @ApiOperation(value = "View a List of all Credit Cards", response = List.class)
    @GetMapping
    public List<CreditCard> findAllCards() {
        return (List) creditCardRepository.findAll();
    }

    //Credit Card Creation API Endpoint

    @PostMapping(value = "/create", produces = "application/json")
    @ApiOperation(value = "Create a new Credit Card", response = ResponseObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Credit Card Created Successfully"),
            @ApiResponse(code = 400, message = "Invalid Credit Card Object"),
            @ApiResponse(code = 409, message = "Credit Card Number already Exists")
    }
    )
    public ResponseObject createCard(@ApiParam(name = "createCreditCard", value = "Create a Credit Card", required = true) @RequestBody CreditCard card) {

        if (card == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Object in request");
        }

        if (card.getCreditCard() == null || card.getCardlimit() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit card or Limit missing in JSON Request");
        }

        String c = card.getCreditCard();
        c = c.replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(c)) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Credit Card Value in Request is Invalid", card);
        }

        if (CardValidationKit.amountValidator(card.getCardlimit()) == false) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Limit Value in JSON Response is Invalid", card);
        }

        Optional<CreditCard> o = creditCardRepository.findById(c);
        if (o.isPresent()) {
            return new ResponseObject(HttpStatus.CONFLICT, "Credit Card Already Exists", o.get());
        }

        String newLimit = "$" + Double.toString(CardValidationKit.amount_from_String(card.getCardlimit()));

        CreditCard newCard = new CreditCard(c, newLimit);
        creditCardRepository.save(newCard);
        return new ResponseObject(HttpStatus.OK, "Request Accepted, Card Created", newCard);

    }

    //Credit Card Charge API Endpoint
    @PutMapping("/charge")
    @ApiOperation(value = "Process a charge to a credit card", response = ResponseObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Credit Card Charged Successfully"),
            @ApiResponse(code = 400, message = "Invalid Credit Card Charge Object"),
            @ApiResponse(code = 404, message = "Credit Card Number does not exist")
    }
    )
    public ResponseObject chargeCard(@ApiParam(name = "CreditCardCharge", value = "Charge to Card", required = true) @RequestBody chargeObject card) {
        if (card == null) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "No JSON Object in Response", null);
        }

        if (card.getCreditCard() == null || card.getCharge() == null) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "JSON has empty Credit Card Number or Charge", card);
        }

        String cardNumberStr = card.getCreditCard().replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(card.getCreditCard())) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Request has Invalid Credit Card Number", card);
        }

        if (!CardValidationKit.amountValidator(card.getCharge())) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Request has Invalid Charge", card);
        }

        Optional<CreditCard> query = creditCardRepository.findById(cardNumberStr);
        if (!query.isPresent()) {
            return new ResponseObject(HttpStatus.NOT_FOUND, "No Credit Card on record found with given card number", card);
        }

        CreditCard foundCard = query.get();
        Double cardLimit = CardValidationKit.amount_from_String(foundCard.getCardlimit());
        Double cardBalance = CardValidationKit.amount_from_String(foundCard.getBalance());
        Double cardCharge = CardValidationKit.amount_from_String(card.getCharge());

        if (cardLimit < cardBalance + cardCharge) {

            return new ResponseObject(HttpStatus.BAD_REQUEST, "Charge exceeds Credit Card Limit", card);
        }
        Double newBalance = cardBalance + cardCharge;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        foundCard.setBalance("$" + newBalance);

        creditCardRepository.save(foundCard);
        return new ResponseObject(HttpStatus.ACCEPTED, "Credit Card charged with request", foundCard);
    }

    @ApiOperation(value = "Process a payment to a credit card", response = ResponseObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Credit Card Charged Successfully"),
            @ApiResponse(code = 400, message = "Invalid Credit Card Payment Object"),
            @ApiResponse(code = 404, message = "Credit Card Number does not exist")
    }
    )
    @PutMapping("/pay")
    public ResponseObject payCard(@ApiParam(name = "CreditCardPayment", value = "Payment to Card", required = true) @RequestBody payObject card) {
        if (card == null) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "No JSON Object in Response", null);
        }

        if (card.getCreditCard() == null || card.getPay() == null) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "JSON has empty Credit Card Number or Payment", card);
        }

        String cardNumberStr = card.getCreditCard().replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(card.getCreditCard())) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Request has Invalid Credit Card Number", card);
        }

        if (!CardValidationKit.amountValidator(card.getPay())) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "Request has Invalid Payment", card);
        }

        Optional<CreditCard> query = creditCardRepository.findById(cardNumberStr);
        if (!query.isPresent()) {
            return new ResponseObject(HttpStatus.NOT_FOUND, "No Credit Card on record found with given card number", card);
        }

        CreditCard foundCard = query.get();
        Double cardLimit = CardValidationKit.amount_from_String(foundCard.getCardlimit());
        Double cardBalance = CardValidationKit.amount_from_String(foundCard.getBalance());
        Double cardPayment = CardValidationKit.amount_from_String(card.getPay());


        Double newBalance = cardBalance - cardPayment;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        foundCard.setBalance("$" + newBalance);

        creditCardRepository.save(foundCard);
        return new ResponseObject(HttpStatus.ACCEPTED, "Credit Card charged with request", foundCard);
    }
}