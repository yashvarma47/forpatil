package com.thinknxt.rba.repository;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.thinknxt.rba.entities.Notifications;

import jakarta.transaction.Transactional;
 
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

	Notifications findByAccountNumber(Long accountNumber);
	//List<Notifications> findByCustomerId(int customerId);
    @Query(value = "SELECT * FROM notifications n where n.customer_id = ?", nativeQuery = true)
	List<Notifications> findAllByCustomerId(Integer customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set txn_confirmation = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforTxnConfirmation(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set security_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforSecurityAlerts(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Low_balance_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforLowBalanceAlerts(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Account_block_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforAccountBlockAlerts(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Account_balance_updates = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforAccountBalanceUpdates(String notificationVia, int customerId);

///
    @Modifying
    @Transactional
    @Query(value="update notifications set txn_confirmation = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforTxnConfirmationNA(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set security_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforSecurityAlertsNA(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Low_balance_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforLowBalanceAlertsNA(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Account_block_alerts = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforAccountBlockAlertsNA(String notificationVia, int customerId);
    @Modifying
    @Transactional
    @Query(value="update notifications set Account_balance_updates = ? where customer_id =?",nativeQuery = true)
    void updateAllByCustomerIdforAccountBalanceUpdatesNA(String notificationVia, int customerId);

}