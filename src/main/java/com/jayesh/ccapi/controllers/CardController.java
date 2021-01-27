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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseObject> createCard(@ApiParam(name = "createCreditCard", value = "Create a Credit Card", required = true) @RequestBody CreditCard card) {

        if (card == null) {
            return new ResponseEntity("Object Missing",HttpStatus.BAD_REQUEST);
        }

        if (card.getCreditCard() == null || card.getCardlimit() == null) {
            return new ResponseEntity("Credit Card Number or Limit Missing",HttpStatus.BAD_REQUEST);
        }

        String c = card.getCreditCard();
        c = c.replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(c)) {
            ResponseObject response = new ResponseObject("Credit Card Value in Request is Invalid", card);
            return  ResponseEntity.badRequest().body(response);
        }

        if (CardValidationKit.amountValidator(card.getCardlimit()) == false) {
            ResponseObject response = new ResponseObject("Limit Value in JSON Response is Invalid", card);
            return  ResponseEntity.badRequest().body(response);
        }

        Optional<CreditCard> o = creditCardRepository.findById(c);
        if (o.isPresent()) {
            ResponseObject response = new ResponseObject( "Credit Card Already Exists", o.get());
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        String newLimit = "$" + Double.toString(CardValidationKit.amount_from_String(card.getCardlimit()));

        CreditCard newCard = new CreditCard(c, newLimit);
        creditCardRepository.save(newCard);
        ResponseObject response = new ResponseObject( "Request Accepted, Card Created", newCard);
        return ResponseEntity.ok().body(response);

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
    public ResponseEntity chargeCard(@ApiParam(name = "CreditCardCharge", value = "Charge to Card", required = true) @RequestBody chargeObject card) {
        if (card == null) {
            ResponseObject response = new ResponseObject( "No JSON Object in Response", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (card.getCreditCard() == null || card.getCharge() == null) {
            ResponseObject response = new ResponseObject("JSON has empty Credit Card Number or Charge", card);
            return ResponseEntity.badRequest().body(response);

        }

        String cardNumberStr = card.getCreditCard().replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(card.getCreditCard())) {
            ResponseObject response =  new ResponseObject("Request has Invalid Credit Card Number", card);
            return ResponseEntity.badRequest().body(response);
        }

        if (!CardValidationKit.amountValidator(card.getCharge())) {
            ResponseObject response =  new ResponseObject( "Request has Invalid Charge", card);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<CreditCard> query = creditCardRepository.findById(cardNumberStr);
        if (!query.isPresent()) {
            ResponseObject response =  new ResponseObject("No Credit Card on record found with given card number", card);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        CreditCard foundCard = query.get();
        Double cardLimit = CardValidationKit.amount_from_String(foundCard.getCardlimit());
        Double cardBalance = CardValidationKit.amount_from_String(foundCard.getBalance());
        Double cardCharge = CardValidationKit.amount_from_String(card.getCharge());

        if (cardLimit < cardBalance + cardCharge) {
            ResponseObject response =  new ResponseObject( "Charge exceeds Credit Card Limit", card);
            return ResponseEntity.badRequest().body(response);
        }
        Double newBalance = cardBalance + cardCharge;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        foundCard.setBalance("$" + newBalance);

        creditCardRepository.save(foundCard);
        ResponseObject response =  new ResponseObject("Credit Card charged with request", foundCard);
        return ResponseEntity.ok().body(response);

    }

    @ApiOperation(value = "Process a payment to a credit card", response = ResponseObject.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Credit Card Charged Successfully"),
            @ApiResponse(code = 400, message = "Invalid Credit Card Payment Object"),
            @ApiResponse(code = 404, message = "Credit Card Number does not exist")
    }
    )
    @PutMapping("/pay")
    public ResponseEntity payCard(@ApiParam(name = "CreditCardPayment", value = "Payment to Card", required = true) @RequestBody payObject card) {
        if (card == null) {
            ResponseObject response = new ResponseObject("No JSON Object in Response", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (card.getCreditCard() == null || card.getPay() == null) {
            ResponseObject response = new ResponseObject("JSON has empty Credit Card Number or Payment", card);
            return ResponseEntity.badRequest().body(response);
        }

        String cardNumberStr = card.getCreditCard().replaceAll("\\s", "");

        if (!CardValidationKit.checkCardFormat(card.getCreditCard())) {
            ResponseObject response = new ResponseObject("Request has Invalid Credit Card Number", card);
            return ResponseEntity.badRequest().body(response);
        }

        if (!CardValidationKit.amountValidator(card.getPay())) {
            ResponseObject response = new ResponseObject( "Request has Invalid Payment", card);
            return ResponseEntity.badRequest().body(response);

        }

        Optional<CreditCard> query = creditCardRepository.findById(cardNumberStr);
        if (!query.isPresent()) {
            ResponseObject response = new ResponseObject("No Credit Card on record found with given card number", card);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        CreditCard foundCard = query.get();
        Double cardLimit = CardValidationKit.amount_from_String(foundCard.getCardlimit());
        Double cardBalance = CardValidationKit.amount_from_String(foundCard.getBalance());
        Double cardPayment = CardValidationKit.amount_from_String(card.getPay());


        Double newBalance = cardBalance - cardPayment;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        foundCard.setBalance("$" + newBalance);

        creditCardRepository.save(foundCard);
        ResponseObject response = new ResponseObject("Credit Card charged with request", foundCard);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}