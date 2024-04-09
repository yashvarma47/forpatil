package com.thinknxt.rba.services;
 
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.thinknxt.rba.entities.LoginDetails;
import com.thinknxt.rba.repository.UserRepository;
@SpringBootConfiguration
@SpringBootTest
public class CustomUserDetailsServiceTest {
 
    @Mock
    private UserRepository userRepository;
 
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
 
    @Test
    public void testLoadUserByUsernameValidUser() {
        // Arrange
        String username = "1234";
        String password = "password";
 
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setUserId(Integer.parseInt(username));
        loginDetails.setPassword(password);
 
        when(userRepository.findByUserId(username)).thenReturn(loginDetails);
 
        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
 
        // Assert
        verify(userRepository, times(1)).findByUserId(username);
        assert(userDetails.getUsername().equals(username));
        assert(userDetails.getPassword().equals(password));
        assert(userDetails.getAuthorities().isEmpty());
    }
 
    @Test
    public void testLoadUserByUsernameUserNotFound() {
        // Arrange
        String username = "nonexistent.user";
 
        when(userRepository.findByUserId(username)).thenThrow(UsernameNotFoundException.class);
//        when(userRepository.findByUserId(username)).thenReturn(Optional.empty());
 
        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
 
        // Verify that the method findByUserId was called
        verify(userRepository, times(1)).findByUserId(username);
    }
}