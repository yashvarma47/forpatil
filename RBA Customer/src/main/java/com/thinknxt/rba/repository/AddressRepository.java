package com.thinknxt.rba.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thinknxt.rba.entities.Address;
import com.thinknxt.rba.config.Generated;

@Repository
@Generated
public interface AddressRepository extends JpaRepository<Address, Integer> {

	Address findByCustomerId(Integer customerId);
}