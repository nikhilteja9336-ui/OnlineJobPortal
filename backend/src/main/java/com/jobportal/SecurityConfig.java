package com.jobportal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder enc(){ return new BCryptPasswordEncoder(); }
 @Bean SecurityFilterChain chain(HttpSecurity http,JwtFilter f,@Value("${cors}") String origins) throws Exception {
  http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .cors(c->c.configurationSource(r->{ var cc=new CorsConfiguration(); cc.setAllowedOriginPatterns(List.of(origins.split(","))); cc.setAllowedMethods(List.of("*")); cc.setAllowedHeaders(List.of("*")); return cc; }))
   .authorizeHttpRequests(a->a.requestMatchers("/","/error","/api/auth/**").permitAll()
     .requestMatchers(HttpMethod.GET,"/api/jobs","/api/jobs/{id:\\d+}").permitAll().anyRequest().authenticated())
   .addFilterBefore(f,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}
