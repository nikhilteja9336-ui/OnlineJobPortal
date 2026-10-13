package com.jobportal;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface JobRepo extends JpaRepository<Job,Long> {
 List<Job> findByPostedByOrderByIdDesc(Long id);
 @Query("select j from Job j where (:q='' or lower(j.title) like lower(concat('%',:q,'%')) or lower(j.company) like lower(concat('%',:q,'%'))) and (:loc='' or lower(j.location) like lower(concat('%',:loc,'%'))) and (:type='' or j.type=:type) order by j.id desc")
 List<Job> search(@Param("q") String q,@Param("loc") String loc,@Param("type") String type);
}
