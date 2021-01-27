package com.jayesh.ccapi.initialize;

import com.jayesh.ccapi.domain.CreditCard;
import com.jayesh.ccapi.repositories.CreditCardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CreditCardInit implements CommandLineRunner {

    CreditCardRepository creditCardRepository;

    public CreditCardInit(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        CreditCard cc = new CreditCard("4111111111111111","$100.00");
        CreditCard cd = new CreditCard("5500000000000004","$150.00");

        creditCardRepository.save(cc);
        creditCardRepository.save(cd);

        System.out.println("Command Line Test");
        System.out.println("Total Cards: " + creditCardRepository.count());



    }
}
