package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Workshop;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkshopRepository extends MongoRepository<Workshop, String> {
    List<Workshop> findByHostUniversity(University hostUniversity);
    List<Workshop> findByDateTimeAfter(LocalDateTime dateTime);
    List<Workshop> findByHostUniversityAndDateTimeAfter(University hostUniversity, LocalDateTime dateTime);
    boolean existsByHostUniversityAndTitle(University hostUniversity, String title);
    List<Workshop> findByRegisteredStudentsContaining(User student);
}