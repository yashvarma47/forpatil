package com.thinknxt.rba.config;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.thinknxt.rba.entities.PasswordResetToken;
import com.thinknxt.rba.repository.PasswordResetTokenRepository;
 
@Service
@Generated
public class PasswordResetTokenService {
	@Autowired
	private  PasswordResetTokenRepository passwordResetTokenRepository;
//	@Autowired
//	private  PasswordResetToken passwordResetToken;
//	PasswordResetTokenService(){
//		("PasswordResetTokenService.PasswordResetTokenService()");
//		("passwordResetToken obj::"+passwordResetToken++);
//	}
    private final Map<String, String> tokenMap = new HashMap<>();
    private final long EXPIRATION_TIME_MS = 3600000; // 1 hour in milliseconds
 
    public String generateTokenForUser(int userId) {
        String token = UUID.randomUUID().toString();
        Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + EXPIRATION_TIME_MS);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(userId);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryTime(expiryTime);
        passwordResetTokenRepository.save(passwordResetToken);
        return token;
    }
}