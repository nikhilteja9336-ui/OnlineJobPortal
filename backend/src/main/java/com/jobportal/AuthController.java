package com.jobportal;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepo users; private final PasswordEncoder enc; private final JwtUtil jwt;
 public AuthController(UserRepo u,PasswordEncoder e,JwtUtil j){ users=u; enc=e; jwt=j; }
 @PostMapping("/register")
 public Map<String,Object> register(@RequestBody Map<String,String> b){
  String email=b.getOrDefault("email","").trim().toLowerCase();
  if(email.isEmpty()||b.getOrDefault("password","").length()<6) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Valid email and a 6+ character password are required");
  if(users.findByEmail(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
  User u=new User(); u.name=b.getOrDefault("name",email); u.email=email; u.password=enc.encode(b.get("password"));
  u.role="EMPLOYER".equals(b.get("role"))?"EMPLOYER":"JOB_SEEKER";
  users.save(u); return Map.of("token",jwt.make(u),"user",u);
 }
 @PostMapping("/login")
 public Map<String,Object> login(@RequestBody Map<String,String> b){
  User u=users.findByEmail(b.getOrDefault("email","").trim().toLowerCase()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));
  if(!enc.matches(b.getOrDefault("password",""),u.password)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
  return Map.of("token",jwt.make(u),"user",u);
 }
}
