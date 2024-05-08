package com.eshop.userservice.service;
import com.eshop.userservice.dto.AuthRequest;
import com.eshop.userservice.dto.AuthResponse;
import com.eshop.userservice.dto.UserRequest;
import com.eshop.userservice.dto.UserResponse;
import com.eshop.userservice.messaging.RabbitMQPublisher;
import com.eshop.userservice.messaging.UserDeletedEvent;
import com.eshop.userservice.model.User;
import com.eshop.userservice.repository.UserRepository;
import com.eshop.userservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RabbitMQPublisher rabbitMQPublisher;
    private final JwtUtil jwtUtil;

    public AuthResponse createUser(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email '" + userRequest.getEmail() + "' is already in use");
        }

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username '" + userRequest.getUsername() + "' is already in use");
        }

        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()))
                .roles(userRequest.getRoles())
                .build();
        userRepository.save(user);

        String accessToken = jwtUtil.generate(String.valueOf(user.getId()), user.getRoles(), "ACCESS");


        return new AuthResponse(accessToken);
    }

    public AuthResponse loginUser(AuthRequest authData) {
        User user = userRepository.findByUsername(authData.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!BCrypt.checkpw(authData.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtUtil.generate(String.valueOf(user.getId()), user.getRoles(), "ACCESS");

        return new AuthResponse(accessToken);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        return convertToDto(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + username));
        return convertToDto(user);
    }

    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
            rabbitMQPublisher.publishUserDeletedMessage(new UserDeletedEvent(userId));
        } else {
            throw new NotFoundException("User with ID " + userId + " not found");
        }
    }

    private UserResponse convertToDto(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .role(user.getRoles())
                .build();
    }
}
