package com.jobportal;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
 private final JwtUtil jwt;
 public JwtFilter(JwtUtil jwt){ this.jwt=jwt; }
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
  String h=req.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")){
   try{ var c=jwt.parse(h.substring(7));
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(Long.valueOf(c.getSubject()),null,List.of(new SimpleGrantedAuthority("ROLE_"+c.get("role",String.class)))));
   }catch(Exception ignored){}
  }
  chain.doFilter(req,res);
 }
}
