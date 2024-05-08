package com.eshop.apigateway;
import com.eshop.apigateway.exception.GlobalWebExceptionHandler;
import com.eshop.apigateway.filter.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;




@SpringBootApplication
@EnableWebFluxSecurity
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }


    private JwtFilter jwtFilter;

    @Bean
    public GlobalWebExceptionHandler jwtWebExceptionHandler() {
        return new GlobalWebExceptionHandler();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.POST, "/eureka/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/products/**").hasAnyRole("USER","ADMIN")
                                .pathMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.POST, "/users/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/users/**").hasAnyRole("USER","ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "users/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.GET, "/cart/**").hasAnyRole("USER","ADMIN")
                                .pathMatchers(HttpMethod.POST, "/cart/**").hasAnyRole("USER","ADMIN")



                                .anyExchange().authenticated()
                )
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}