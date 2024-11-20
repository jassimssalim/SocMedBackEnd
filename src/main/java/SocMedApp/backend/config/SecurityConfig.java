package SocMedApp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/home").authenticated()  // Restrict access to "/home"
                        .anyRequest().permitAll()  // Allow all other paths
                )
                .formLogin(login -> login
                        .loginPage("/Entry")  // Custom login page mapped to "/Entry"
                        .defaultSuccessUrl("/home", true)  // Redirect to /home after successful login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // Logout endpoint
                        .logoutSuccessUrl("/Entry")  // Redirect after logout
                )
                .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity in development
                .sessionManagement(session -> session
                        .maximumSessions(1)  // Limit to one session per user
                        .maxSessionsPreventsLogin(false)  // Allow login if session exists
                );

        return http.build();
    }
}
