package com.thinknxt.rba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.entities.BenificiaryAccount;

@Generated
public interface BenificiaryAccountRepository extends JpaRepository<BenificiaryAccount, Integer> {
	List<BenificiaryAccount> findByCustomerid(int customerId);

	boolean existsByBenaccountnumberAndCustomerid(long benaccountnumber, int customerid);
}