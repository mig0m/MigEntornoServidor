package org.goya.daw2.dwes.d3ud3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.requestMatchers("/", "/home", "/login2").permitAll().requestMatchers("/admin").hasRole("ADMIN").anyRequest().authenticated()).formLogin(form -> form.loginPage("/login2").permitAll()).logout(logout -> logout.logoutSuccessUrl("/login2?logout").permitAll());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails usuario = User.builder().username("usuario").password(encoder.encode("usuario123")).roles("USER").build();
        UserDetails administrador = User.builder().username("admin").password(encoder.encode("admin123")).roles("USER", "ADMIN").build();
        return new InMemoryUserDetailsManager(usuario, administrador);
    }
}