package com.eshop.apigateway.service;


import com.eshop.apigateway.dto.UserRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AuthService implements ReactiveUserDetailsService {

    private final WebClient.Builder webClientBuilder;

    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.create(userDetailsMonoSink -> {
            Mono<UserRequest> dtoMono = webClientBuilder.build().get().uri("http://USER-SERVICE/users/name/{username}", username).retrieve().onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty()).bodyToMono(UserRequest.class);
            dtoMono.subscribe(userDTO -> {
                System.out.println("userDTO= " + userDTO);
                userDetailsMonoSink.success(userDTO.getUsername() == null ? null : new User(userDTO.getUsername(), userDTO.getPassword(), List.of(new SimpleGrantedAuthority(userDTO.getRoles()))));
            });

        });

    }
}