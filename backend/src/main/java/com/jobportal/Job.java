package com.jobportal;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class Job {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String title, company, location, type, salary;
 @Column(length=4000) public String description;
 public Long postedBy;
 public LocalDate postedAt;
}
