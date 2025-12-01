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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private AutenticacaoFilter securityFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll();

                    req.requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll();
                    req.requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll();

                    req.requestMatchers(HttpMethod.GET, "/api/categorias", "/api/categorias/**").authenticated();
                    req.requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/api/tags", "/api/tags/**").authenticated();
                    req.requestMatchers(HttpMethod.POST, "/api/tags").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/tags/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/tags/**").hasRole("ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios/email/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios/existe/**").hasRole("ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/api/assinaturas/minhas").authenticated();
                    req.requestMatchers(HttpMethod.GET, "/api/assinaturas").hasRole("ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/api/pagamento/meus").authenticated();
                    req.requestMatchers(HttpMethod.GET, "/api/pagamentos").hasRole("ADMIN");

                    req.requestMatchers(HttpMethod.GET, "/api/metodos-pagamento/meus").authenticated();
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD", "TRACE", "CONNECT"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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