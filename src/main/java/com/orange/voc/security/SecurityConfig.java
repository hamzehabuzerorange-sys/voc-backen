package com.orange.voc.security;

import com.orange.voc.domain.*;
import com.orange.voc.repo.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService userDetailsService(UserRepository users) {
        return username -> {
            AppUser u = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> new UsernameNotFoundException(username));
            return new User(u.getUsername(), u.getPasswordHash(),
                    u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toList()));
        };
    }

    @Bean SecurityFilterChain chain(HttpSecurity http, ApiKeyRepository keys) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/v1/**").hasRole("API_CLIENT")
                .antMatchers("/api/admin/**").authenticated()
                .anyRequest().denyAll().and()
            .addFilterBefore(new ApiKeyFilter(keys), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))); // swap for JWT / corporate SSO (OIDC) before production
        return http.build();
    }

    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${voc.cors-origins}") String origins) {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(Arrays.asList(origins.split(",")));
        c.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-API-Key", "Accept"));
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**", c); return s;
    }

    /**
     * Bootstrap admin: when voc.bootstrap-admin-password is set, the user "admin" is created or RESET to that password
     * (active, with ADMIN role) on every start. Clear the property in production.
     */
    @Bean CommandLineRunner seedAdmin(UserRepository users, RoleRepository roles, PasswordEncoder enc, @Value("${voc.bootstrap-admin-password:}") String pw) {
        return args -> {
            if (pw == null || pw.isEmpty()) return;
            AppUser u = users.findByUsername("admin").orElseGet(AppUser::new);
            u.setUsername("admin"); u.setPasswordHash(enc.encode(pw)); u.setActive(true);
            roles.findByName("ADMIN").ifPresent(r -> { if (u.getRoles().stream().noneMatch(x -> "ADMIN".equals(x.getName()))) u.getRoles().add(r); });
            users.save(u);
        };
    }
}
