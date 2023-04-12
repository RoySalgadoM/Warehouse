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
    private static final String ADMIN = "ADMIN";
    private static final String WAREHOUSER = "WAREHOUSER";
    private static final String INVOICER = "INVOICER";
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
                    .requestMatchers("/user/profile", "/user/getme", "/user/updatePassword").hasAnyAuthority(ADMIN, WAREHOUSER, INVOICER)
                    .requestMatchers("/product/**").hasAnyAuthority(WAREHOUSER, ADMIN)
                    .requestMatchers("/warehouse/list").hasAnyAuthority(WAREHOUSER, INVOICER, ADMIN)

                    .requestMatchers( "/user/**", "/log/**", "/warehouse/**", "/supplier/**", "/area/**", "/order/**").hasAuthority(ADMIN)
                    .requestMatchers("/entry/**", "/report/entry/**").hasAuthority(WAREHOUSER)
                    .requestMatchers("/output/**", "/report/output/**").hasAnyAuthority(INVOICER)
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/user/profile")
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