package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.UserSkill;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserSkillRepository extends MongoRepository<UserSkill, String> {
    List<UserSkill> findByUser(User user);
    List<UserSkill> findByUserAndType(User user, UserSkill.SkillType type);
    List<UserSkill> findBySkillAndType(Skill skill, UserSkill.SkillType type);
}