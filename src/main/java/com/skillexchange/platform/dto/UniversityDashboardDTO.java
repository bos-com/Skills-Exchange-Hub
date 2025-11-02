package com.skillexchange.platform.dto;

import java.util.List;
import java.util.Map;

public class UniversityDashboardDTO {
    private long totalStudents;
    private long activeExchanges;
    private List<TopSkillDTO> topSkills;
    private long totalProjects;
    private long totalWorkshops;
    private long upcomingWorkshops;
    private long pendingPartnershipProposals;

    // Constructors
    public UniversityDashboardDTO() {}

    // Getters and Setters
    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getActiveExchanges() {
        return activeExchanges;
    }

    public void setActiveExchanges(long activeExchanges) {
        this.activeExchanges = activeExchanges;
    }

    public List<TopSkillDTO> getTopSkills() {
        return topSkills;
    }

    public void setTopSkills(List<TopSkillDTO> topSkills) {
        this.topSkills = topSkills;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getTotalWorkshops() {
        return totalWorkshops;
    }

    public void setTotalWorkshops(long totalWorkshops) {
        this.totalWorkshops = totalWorkshops;
    }

    public long getUpcomingWorkshops() {
        return upcomingWorkshops;
    }

    public void setUpcomingWorkshops(long upcomingWorkshops) {
        this.upcomingWorkshops = upcomingWorkshops;
    }

    public long getPendingPartnershipProposals() {
        return pendingPartnershipProposals;
    }

    public void setPendingPartnershipProposals(long pendingPartnershipProposals) {
        this.pendingPartnershipProposals = pendingPartnershipProposals;
    }

    // Inner class for top skills
    public static class TopSkillDTO {
        private String skillName;
        private long count;

        public TopSkillDTO() {}

        public TopSkillDTO(String skillName, long count) {
            this.skillName = skillName;
            this.count = count;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}