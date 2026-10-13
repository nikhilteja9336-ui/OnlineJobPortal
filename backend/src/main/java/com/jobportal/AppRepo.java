package com.jobportal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AppRepo extends JpaRepository<Application,Long> {
 List<Application> findByJobId(Long id);
 List<Application> findBySeekerIdOrderByIdDesc(Long id);
 boolean existsByJobIdAndSeekerId(Long j, Long s);
}
