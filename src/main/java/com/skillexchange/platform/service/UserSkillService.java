package com.skillexchange.platform.service;

import com.skillexchange.platform.entity.UserSkill;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.Skill;
import com.skillexchange.platform.repository.UserSkillRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.SkillRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public UserSkillService(UserSkillRepository userSkillRepository, UserRepository userRepository, SkillRepository skillRepository) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public UserSkill offerSkill(String userId, UserSkill userSkill) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userSkill.setUser(user);
            userSkill.setType(UserSkill.SkillType.OFFERED);
            return userSkillRepository.save(userSkill);
        }
        return null;
    }

    public UserSkill wantSkill(String userId, UserSkill userSkill) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userSkill.setUser(user);
            userSkill.setType(UserSkill.SkillType.WANTED);
            return userSkillRepository.save(userSkill);
        }
        return null;
    }

    public List<UserSkill> getOfferedSkills(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> userSkillRepository.findByUserAndType(user, UserSkill.SkillType.OFFERED))
                .orElse(List.of());
    }

    public List<UserSkill> getWantedSkills(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> userSkillRepository.findByUserAndType(user, UserSkill.SkillType.WANTED))
                .orElse(List.of());
    }

    public void removeUserSkill(String userSkillId) {
        userSkillRepository.deleteById(userSkillId);
    }
}