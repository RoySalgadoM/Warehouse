package mx.edu.utez.warehouse.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig{


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        try {
            http
                    .authorizeRequests()
                    .requestMatchers("/**").permitAll()
                    .requestMatchers("/").access("hasAnyAuthority('ADMIN')")
                    /*
                    .requestMatchers("/").access("hasAnyAuthority('WAREHOUSER')")
                    .requestMatchers("/").access("hasAnyAuthority('INVOICER')")
                    */
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/dashboard")
                    .and()
                    .logout().permitAll();
            return http.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}