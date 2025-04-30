package com.sivalabs.ft.features.config;

import com.sivalabs.ft.features.integration.PublisherType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(c -> c.requestMatchers(
                                "/favicon.ico", "/actuator/**", "/error", "/swagger-ui/**", "/v3/api-docs**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/releases/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/features/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/version/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/contact/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(CorsConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}

class PropertyFallbackPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        String publisherType = env.getProperty("ft.events.publisher");
        if (!PublisherType.KAFKA.name().equals(publisherType)
                && !PublisherType.DUMB.name().equals(publisherType)) {
            MutablePropertySources propertySources = env.getPropertySources();
            Map<String, Object> map = new HashMap<>();
            map.put("ft.events.publisher", PublisherType.DUMB.name());
            propertySources.addFirst(new MapPropertySource("fallbackPublisher", map));
        }
    }
}
