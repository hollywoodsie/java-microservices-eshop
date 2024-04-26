package com.eshop.userservice.service;

import com.eshop.userservice.dto.UserRequest;
import com.eshop.userservice.dto.UserResponse;
import com.eshop.userservice.model.User;
import com.eshop.userservice.repository.UserRepository;
import com.eshop.userservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void createUser(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email '" + userRequest.getEmail() + "' is already in use");
        }

        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username '" + userRequest.getUsername() + "' is already in use");
        }

        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(DigestUtils.sha256Hex(userRequest.getPassword()))
                .roles(userRequest.getRoles())
                .build();
        userRepository.save(user);
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

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NotFoundException("User not found with username: " + username);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponse convertToDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles())
                .build();
    }
}
