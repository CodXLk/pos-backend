package com.sms.businesslogic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.sms.businesslogic.entity.Permission.*;
import static com.sms.businesslogic.entity.Permission.CUSTOMER_DELETE;
import static com.sms.businesslogic.entity.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                .permitAll()

                .requestMatchers(GET,"/api/v1/category/**").hasAnyAuthority(ADMIN_READ.getPermission(),CUSTOMER_READ.getPermission())
                .requestMatchers(POST,"/api/v1/category/**").hasAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT,"/api/v1/category/**").hasAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE,"/api/v1/category/**").hasAuthority(ADMIN_DELETE.getPermission())

                .requestMatchers(GET,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_READ.getPermission(), CUSTOMER_READ.getPermission(), DELIVERY_PERSON_READ.getPermission())
                .requestMatchers(POST,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), DELIVERY_PERSON_CREATE.getPermission())
                .requestMatchers(PUT,"/api/v1/delivery/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), DELIVERY_PERSON_UPDATE.getPermission())
                .requestMatchers(DELETE,"/api/v1/delivery/**").hasAuthority(ADMIN_DELETE.getPermission())

                .requestMatchers(GET,"/api/v1/order/**").hasAnyAuthority(ADMIN_READ.getPermission(), CUSTOMER_READ.getPermission(), INVENTORY_KEEPER_READ.getPermission(), DELIVERY_PERSON_READ.getPermission())
                .requestMatchers(POST,"/api/v1/order/user/**").hasAnyAuthority(ADMIN_CREATE.getPermission(),CUSTOMER_CREATE.getPermission())
                .requestMatchers(PUT,"/api/v1/order/**").hasAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE,"/api/v1/order/**").hasAuthority(ADMIN_DELETE.getPermission())

                .requestMatchers(GET,"/api/v1/product/**").hasAnyAuthority(ADMIN_READ.getPermission(), CUSTOMER_READ.getPermission(), INVENTORY_KEEPER_READ.getPermission())
                .requestMatchers(POST,"/api/v1/product/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), INVENTORY_KEEPER_CREATE.getPermission())
                .requestMatchers(PUT,"/api/v1/product/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(),INVENTORY_KEEPER_UPDATE.getPermission())
                .requestMatchers(DELETE,"/api/v1/product/**").hasAuthority(ADMIN_DELETE.getPermission())

                .requestMatchers(GET,"/api/v1/payment/**").hasAnyAuthority(ADMIN_READ.getPermission(), CUSTOMER_READ.getPermission())
                .requestMatchers(POST,"/api/v1/payment/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), CUSTOMER_READ.getPermission())
                .requestMatchers(PUT,"/api/v1/payment/**").hasAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE,"/api/v1/payment/**").hasAuthority(ADMIN_DELETE.getPermission())

                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}