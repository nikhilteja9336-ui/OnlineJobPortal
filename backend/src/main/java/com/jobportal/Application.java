package com.jobportal;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class Application {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public Long jobId, seekerId;
 public String jobTitle, company, seekerName, seekerEmail;
 @Column(length=2000) public String coverLetter;
 public String status; // APPLIED, SHORTLISTED, REJECTED
 public LocalDate appliedAt;
}
