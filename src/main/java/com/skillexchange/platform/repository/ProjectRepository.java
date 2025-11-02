package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Project;
import com.skillexchange.platform.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOwner(User owner);
}