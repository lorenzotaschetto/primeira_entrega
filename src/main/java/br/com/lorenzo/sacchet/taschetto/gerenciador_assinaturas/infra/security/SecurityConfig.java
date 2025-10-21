package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.infra.security;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.exception.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Clock;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private AutenticacaoFilter securityFilter;

    @Autowired // <-- Injete seu handler customizado
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll();

                    req.requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll();
                    req.requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll();

                    req.requestMatchers("/api/categorias/**").hasRole("ADMIN");
                    req.requestMatchers("/api/tags/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios/email/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios/existe/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/assinaturas").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/pagamentos").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/metodos-pagamento").hasRole("ADMIN");

                    req.requestMatchers("/api/assinaturas/**").authenticated();
                    req.requestMatchers("/api/pagamentos/**").authenticated();
                    req.requestMatchers("/api/metodos-pagamento/**").authenticated();

                    req.requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated();
                    req.requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").authenticated();

                    req.anyRequest().authenticated();
                })
                .exceptionHandling(eh -> eh
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}