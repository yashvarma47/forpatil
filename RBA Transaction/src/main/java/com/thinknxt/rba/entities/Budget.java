package com.thinknxt.rba.entities;

import java.math.BigDecimal;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "budget")
@Generated
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "account_number",nullable = false)
    private long accountNumber;

////    @Enumerated(EnumType.STRING)
////    @Column(name = "category",nullable = false)
////    private TransactionCategory category;
//
//    @Column(name = "limit_amount",nullable = false)
//    private double limitAmount;
	
	
	@Column(name = "budget_data", columnDefinition = "JSON", nullable = false)
    private String budgetData;

}


