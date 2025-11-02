package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.MatchDTO;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.UserSkill;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchmakingService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public MatchmakingService(UserRepository userRepository, UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    /**
     * Find matches for a user based on complementary skills
     * 
     * Pseudo-code logic:
     * for each user U in DB:
     *   score = number of complementary skill pairs with currentUser
     * return top N by score
     */
    public List<MatchDTO> findMatches(String userId, int limit) {
        Optional<User> currentUserOpt = userRepository.findById(userId);
        if (currentUserOpt.isEmpty()) {
            return new ArrayList<>();
        }

        User currentUser = currentUserOpt.get();
        List<User> allUsers = userRepository.findAll();

        // Calculate match scores for all users
        List<MatchScore> matchScores = new ArrayList<>();

        for (User user : allUsers) {
            // Skip the current user
            if (user.getId().equals(userId)) {
                continue;
            }

            int score = calculateMatchScore(currentUser, user);
            if (score > 0) {
                matchScores.add(new MatchScore(user, score));
            }
        }

        // Sort by score (descending) and limit results
        return matchScores.stream()
                .sorted((ms1, ms2) -> Integer.compare(ms2.score, ms1.score))
                .limit(limit)
                .map(ms -> convertToMatchDTO(ms.user, ms.score))
                .collect(Collectors.toList());
    }

    /**
     * Calculate match score based on complementary skills
     * Score = number of skills user A wants that user B offers
     *       + number of skills user B wants that user A offers
     */
    private int calculateMatchScore(User user1, User user2) {
        // Get skills offered and wanted by each user
        List<UserSkill> user1Skills = userSkillRepository.findByUser(user1);
        List<UserSkill> user2Skills = userSkillRepository.findByUser(user2);

        Map<String, UserSkill> user1OfferedSkills = user1Skills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.OFFERED)
                .collect(Collectors.toMap(us -> us.getSkill().getId(), us -> us));

        Map<String, UserSkill> user2OfferedSkills = user2Skills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.OFFERED)
                .collect(Collectors.toMap(us -> us.getSkill().getId(), us -> us));

        Map<String, UserSkill> user1WantedSkills = user1Skills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.WANTED)
                .collect(Collectors.toMap(us -> us.getSkill().getId(), us -> us));

        Map<String, UserSkill> user2WantedSkills = user2Skills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.WANTED)
                .collect(Collectors.toMap(us -> us.getSkill().getId(), us -> us));

        // Calculate complementary matches
        int score = 0;

        // Count how many skills user1 wants that user2 offers
        for (String skillId : user1WantedSkills.keySet()) {
            if (user2OfferedSkills.containsKey(skillId)) {
                score++;
            }
        }

        // Count how many skills user2 wants that user1 offers
        for (String skillId : user2WantedSkills.keySet()) {
            if (user1OfferedSkills.containsKey(skillId)) {
                score++;
            }
        }

        return score;
    }

    private MatchDTO convertToMatchDTO(User user, int score) {
        List<UserSkill> userSkills = userSkillRepository.findByUser(user);
        
        Set<String> skillsOffered = userSkills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.OFFERED)
                .map(us -> us.getSkill().getName())
                .collect(Collectors.toSet());
                
        Set<String> skillsWanted = userSkills.stream()
                .filter(us -> us.getType() == UserSkill.SkillType.WANTED)
                .map(us -> us.getSkill().getName())
                .collect(Collectors.toSet());

        return new MatchDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBio(),
                skillsOffered,
                skillsWanted,
                score
        );
    }

    /**
     * Helper class to hold user and match score
     */
    private static class MatchScore {
        User user;
        int score;

        MatchScore(User user, int score) {
            this.user = user;
            this.score = score;
        }
    }
}