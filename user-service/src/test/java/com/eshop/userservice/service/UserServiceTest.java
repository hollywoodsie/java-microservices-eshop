package com.eshop.userservice.service;

import com.eshop.userservice.dto.AuthRequest;
import com.eshop.userservice.dto.AuthResponse;
import com.eshop.userservice.dto.UserRequest;
import com.eshop.userservice.model.User;
import com.eshop.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateUser_Success() {

        UserRequest userRequest = new UserRequest("testusername", "testemail@example.com", "password", "USER");
        User user = User.builder().username("testusername").email("testemail@example.com").password("password").roles("ROLE_USER").build();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generate(anyString(), anyString(), anyString())).thenReturn("testToken");

        AuthResponse authResponse = userService.createUser(userRequest);
        assertNotNull(authResponse);
        assertEquals("testToken", authResponse.getAccessToken());
    }

    @Test
    public void testLoginUser_Success() {

        AuthRequest authRequest = new AuthRequest("testusername", "password", "USER");
        User user = User.builder().username("testusername").email("testemail@example.com").password("$2a$12$wP.i//j4pbfMiijlNpqBm.PQhcYdRJTWhGXpbgaW4zXn5phjRNXIe").roles("USER").build();
        when(userRepository.findByUsername(eq("testusername"))).thenReturn(Optional.of(user));
        when(jwtUtil.generate(anyString(), anyString(), anyString())).thenReturn("testToken");

        AuthResponse authResponse = userService.loginUser(authRequest);
        assertNotNull(authResponse);
        assertEquals("testToken", authResponse.getAccessToken());
    }

    @Test
    public void testCreateUser_WithExistingEmail() {
        UserRequest userRequest = new UserRequest("testusername", "existingemail@example.com", "password", "USER");
        when(userRepository.existsByEmail("existingemail@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userRequest);
        });
        assertEquals("Email 'existingemail@example.com' is already in use", exception.getMessage());
    }

    @Test
    public void testCreateUser_WithExistingUsername() {
        UserRequest userRequest = new UserRequest("existingusername", "testemail@example.com", "password", "USER");
        when(userRepository.existsByUsername("existingusername")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userRequest);
        });
        assertEquals("Username 'existingusername' is already in use", exception.getMessage());
    }

    @Test
    public void testLoginUser_IncorrectPassword() {
        AuthRequest authRequest = new AuthRequest("testusername", "incorrectpassword", "USER");
        User user = User.builder().username("testusername").email("testemail@example.com").password("$2a$12$qZtHZx8uhfKCz3/jFCyt2eyK3dODfiEr3FdTnWoyNLiVj7J1mPSH2").roles("USER").build();
        when(userRepository.findByUsername("testusername")).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.loginUser(authRequest);
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    public void testLoginUser_NonexistentUsername() {

        AuthRequest authRequest = new AuthRequest("nonexistentusername", "password", "USER");
        when(userRepository.findByUsername("nonexistentusername")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.loginUser(authRequest);
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }

}
