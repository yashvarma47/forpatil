package com.thinknxt.rba.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

import com.thinknxt.rba.config.Generated;

@Entity
@Data
@Table(name = "transaction")
@Generated
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "transaction_status", nullable = false)
    private String transactionStatus;

    @Column(name = "transaction_mode", nullable = false)
    private String transactionMode;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

}


