package com.jobportal;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
@SpringBootApplication @RestController
public class JobPortalApplication {
 public static void main(String[] a){ SpringApplication.run(JobPortalApplication.class,a); }
 @GetMapping("/") public String health(){ return "Job Portal API is running"; }
 @Bean CommandLineRunner seed(UserRepo users,JobRepo jobs,PasswordEncoder enc){ return x->{
  if(users.count()>0) return;
  User e=new User(); e.name="Demo Employer"; e.email="employer@demo.com"; e.password=enc.encode("demo123"); e.role="EMPLOYER"; users.save(e);
  User s=new User(); s.name="Demo Seeker"; s.email="seeker@demo.com"; s.password=enc.encode("demo123"); s.role="JOB_SEEKER"; users.save(s);
  String[][] d={{"Java Full Stack Developer","TechNova","Hyderabad","Full-time","₹12-18 LPA"},{"React Frontend Engineer","PixelWorks","Bengaluru","Remote","₹10-16 LPA"},{"Spring Boot Backend Developer","CloudKart","Pune","Full-time","₹14-20 LPA"},{"Software Engineering Intern","InfoBridge","Chennai","Internship","₹25k/month"},{"UI/UX Designer","DesignHub","Mumbai","Part-time","₹6-9 LPA"},{"DevOps Engineer","ScaleOps","Hyderabad","Contract","₹18-26 LPA"}};
  for(String[] r:d){ Job j=new Job(); j.title=r[0]; j.company=r[1]; j.location=r[2]; j.type=r[3]; j.salary=r[4]; j.postedBy=e.id; j.postedAt=LocalDate.now();
   j.description="We are hiring a "+r[0]+" to join "+r[1]+". You will build and ship features, collaborate with a friendly team, and grow your career. Strong problem-solving skills and good communication are required."; jobs.save(j); }
 }; }
}
