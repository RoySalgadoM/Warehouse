package mx.edu.utez.warehouse.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig{
    private static final Logger logger = LogManager.getLogger(WebSecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        try {
            http
                    .authorizeHttpRequests()
                    .requestMatchers("/css/**").permitAll()
                    .requestMatchers("/js/**").permitAll()
                    .requestMatchers("/AdminLTE/**").permitAll()
                    .requestMatchers("/errors/**").permitAll()
                    .requestMatchers("/**").hasAnyAuthority("ADMIN")

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
                    .logout().permitAll()
                    .and().exceptionHandling().accessDeniedPage("/errors/403");
            return http.build();
        } catch (Exception exception) {
            logger.error("WEBSECURITYCONFIG ERROR: {}", exception.getMessage());

        }
        return null;
    }

}