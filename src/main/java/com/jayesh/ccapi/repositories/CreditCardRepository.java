package com.jayesh.ccapi.repositories;

import com.jayesh.ccapi.domain.CreditCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard,String> {

}
