package com.jayesh.ccapi;

import com.jayesh.ccapi.controllers.CardController;
import com.jayesh.ccapi.domain.CreditCard;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebMvcTest(CardController.class)
class CcapiApplicationTests {

    @Test
    public void testCreateSuccess() throws Exception{
        CreditCard card = new CreditCard("4111 1111 1111 1111","$100.00");

        
    }

}
