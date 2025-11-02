package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SkillRepository extends MongoRepository<Skill, String> {
    Optional<Skill> findByName(String name);
}