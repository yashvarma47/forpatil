package com.thinknxt.rba.entities;
 
import java.sql.Date;
 
import com.thinknxt.rba.config.Generated;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
@Generated
public class Notifications {
 
	@Id
	@Column(name = "account_number",columnDefinition = "DEFAULT '0'")
	private Long accountNumber;
	@Column(name = "customer_id")
	private Integer customerId;
	@Column(name = "Txn_Confirmation",columnDefinition = "DEFAULT 'NA'")
	private String txnConfirmation;
 
	@Column(name = "Account_balance_updates",columnDefinition = "DEFAULT 'NA'")
	private String accountBalanceUpdates;
	@Column(name = "Low_balance_alerts",columnDefinition = "DEFAULT 'NA'")
	private String lowBalanceAlerts;
 
	@Column(name = "Security_alerts",columnDefinition = "DEFAULT 'NA'")
	private String securityAlerts;
 
	@Column(name = "Account_Block_Alerts",columnDefinition = "DEFAULT 'NA'")
	private String accountBlockAlerts;
 
}