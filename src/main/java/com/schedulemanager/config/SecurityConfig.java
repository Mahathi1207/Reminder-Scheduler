package com.schedulemanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

   /* @Bean
    public InMemoryUserDetailsManager userManager(){
        UserDetails user= User.builder()
                .username("user")
                .password("{noop}user")
                .roles("Employee")
                .build();
        return new InMemoryUserDetailsManager(user);
    }*/
    @Bean
    public UserDetailsManager authenticate(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }
}
