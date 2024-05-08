package com.eshop.apigateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class JwtFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String userId = extractUserIdFrom(jwtToken);
            exchange.getRequest().mutate().headers(httpHeaders -> {
                if (!httpHeaders.containsKey("X-UserId")) {
                    httpHeaders.add("X-UserId", userId);
                }
            });

            UsernamePasswordAuthenticationToken authenticationToken = validateToken(jwtToken);

            if (authenticationToken != null && authenticationToken.isAuthenticated()) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                exchange.getAttributes().put(SecurityContext.class.getName(), context);

                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
            }
        }
        return chain.filter(exchange);
    }

    public UsernamePasswordAuthenticationToken validateToken(String jwtToken) throws JwtException {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);

        Date expiration = claims.getBody().get("exp", Date.class);

        if (expiration != null && expiration.before(new Date())) {
            throw new JwtException("Token is expired");
        }

        String userId = claims.getBody().get("id", String.class);
        String role = claims.getBody().get("role", String.class);

        if (userId != null && role != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            return new UsernamePasswordAuthenticationToken(userId, null, authorities);
        }

        throw new JwtException("Token is not valid");
    }

    private String extractUserIdFrom(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtToken);

        return claims.getBody().get("id", String.class);
    }
}