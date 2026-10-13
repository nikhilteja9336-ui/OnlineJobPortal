package com.jobportal;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api")
public class ApiController {
 private final JobRepo jobs; private final AppRepo apps; private final UserRepo users;
 public ApiController(JobRepo j,AppRepo a,UserRepo u){ jobs=j; apps=a; users=u; }
 private static ResponseStatusException err(HttpStatus s,String m){ return new ResponseStatusException(s,m); }
 private Job job(Long id){ return jobs.findById(id).orElseThrow(()->err(HttpStatus.NOT_FOUND,"Job not found")); }
 private Job owned(Long id,Authentication a){ Job j=job(id); if(!j.postedBy.equals(a.getPrincipal())) throw err(HttpStatus.FORBIDDEN,"Not your job"); return j; }

 @GetMapping("/jobs") public List<Job> list(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="") String location,@RequestParam(defaultValue="") String type){ return jobs.search(q.trim(),location.trim(),type); }
 @GetMapping("/jobs/{id}") public Job get(@PathVariable Long id){ return job(id); }
 @GetMapping("/jobs/mine") @PreAuthorize("hasRole('EMPLOYER')") public List<Job> mine(Authentication a){ return jobs.findByPostedByOrderByIdDesc((Long)a.getPrincipal()); }
 @PostMapping("/jobs") @PreAuthorize("hasRole('EMPLOYER')") public Job post(@RequestBody Job j,Authentication a){ j.id=null; j.postedBy=(Long)a.getPrincipal(); j.postedAt=LocalDate.now(); return jobs.save(j); }
 @DeleteMapping("/jobs/{id}") @PreAuthorize("hasRole('EMPLOYER')") public void del(@PathVariable Long id,Authentication a){ Job j=owned(id,a); apps.deleteAll(apps.findByJobId(id)); jobs.delete(j); }

 @PostMapping("/jobs/{id}/apply") @PreAuthorize("hasRole('JOB_SEEKER')")
 public Application apply(@PathVariable Long id,@RequestBody Map<String,String> b,Authentication a){
  Long uid=(Long)a.getPrincipal(); Job j=job(id);
  if(apps.existsByJobIdAndSeekerId(id,uid)) throw err(HttpStatus.CONFLICT,"You already applied to this job");
  User u=users.findById(uid).orElseThrow(()->err(HttpStatus.UNAUTHORIZED,"User not found"));
  Application ap=new Application(); ap.jobId=id; ap.jobTitle=j.title; ap.company=j.company; ap.seekerId=uid; ap.seekerName=u.name; ap.seekerEmail=u.email;
  ap.coverLetter=b.getOrDefault("coverLetter",""); ap.status="APPLIED"; ap.appliedAt=LocalDate.now(); return apps.save(ap);
 }
 @GetMapping("/applications/mine") @PreAuthorize("hasRole('JOB_SEEKER')") public List<Application> myApps(Authentication a){ return apps.findBySeekerIdOrderByIdDesc((Long)a.getPrincipal()); }
 @GetMapping("/jobs/{id}/applications") @PreAuthorize("hasRole('EMPLOYER')") public List<Application> applicants(@PathVariable Long id,Authentication a){ owned(id,a); return apps.findByJobId(id); }
 @PutMapping("/applications/{id}/status") @PreAuthorize("hasRole('EMPLOYER')")
 public Application status(@PathVariable Long id,@RequestBody Map<String,String> b,Authentication a){
  Application ap=apps.findById(id).orElseThrow(()->err(HttpStatus.NOT_FOUND,"Application not found")); owned(ap.jobId,a);
  String s=b.getOrDefault("status","");
  if(!List.of("APPLIED","SHORTLISTED","REJECTED").contains(s)) throw err(HttpStatus.BAD_REQUEST,"Invalid status");
  ap.status=s; return apps.save(ap);
 }
}
