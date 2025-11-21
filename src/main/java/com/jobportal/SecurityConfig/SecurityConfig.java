package com.jobportal.SecurityConfig;

import com.jobportal.SecurityConfig.CustomLoginSuccessHandler;
import com.jobportal.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomLoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        // Public pages
                        .requestMatchers(
                                "/register",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/h2-console/**"
                        ).permitAll()

                        // USER routes
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/applications/apply/**").hasRole("USER")
                        .requestMatchers("/applications/my").hasRole("USER")

                        // Download resumes — both USER and RECRUITER allowed
                        .requestMatchers("/applications/resume/**").hasAnyRole("USER", "RECRUITER")

                        // RECRUITER routes
                        .requestMatchers("/recruiter/**").hasRole("RECRUITER")

                        // Anything else requires login
                        .anyRequest().authenticated()
                )

                // Login page
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                // Disable CSRF for H2 db console
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))

                // H2 console frame settings
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
