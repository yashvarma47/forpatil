package com.thinknxt.rba.entities;
import java.time.LocalDateTime;

import com.thinknxt.rba.config.Generated;
import com.thinknxt.rba.utils.TransactionCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "savings_transaction")
@Generated
public class SavingsTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "transaction_amount", nullable = false)
    private double transactionAmount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "remaining_balance", nullable = false)
    private double remainingBalance;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
    
    @Column(name = "narratives", nullable = false)
    private String narratives;

    @Column(name = "recipient", nullable = false)
    private Long recipient;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_category", nullable = false)
    private TransactionCategory transactionCategory;

    
    @ManyToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id", insertable = false, updatable = false)
    private TransactionEntity transaction;


}
