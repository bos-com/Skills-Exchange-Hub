package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.UniversityDashboardDTO;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.entity.Project;
import com.skillexchange.platform.entity.Workshop;
import com.skillexchange.platform.entity.PartnershipProposal;
import com.skillexchange.platform.repository.UniversityRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import com.skillexchange.platform.repository.ProjectRepository;
import com.skillexchange.platform.repository.WorkshopRepository;
import com.skillexchange.platform.repository.PartnershipProposalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UniversityDashboardService {

    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final ProjectRepository projectRepository;
    private final WorkshopRepository workshopRepository;
    private final PartnershipProposalRepository partnershipProposalRepository;

    public UniversityDashboardService(UniversityRepository universityRepository,
                                     UserRepository userRepository,
                                     ExchangeRequestRepository exchangeRequestRepository,
                                     ProjectRepository projectRepository,
                                     WorkshopRepository workshopRepository,
                                     PartnershipProposalRepository partnershipProposalRepository) {
        this.universityRepository = universityRepository;
        this.userRepository = userRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.projectRepository = projectRepository;
        this.workshopRepository = workshopRepository;
        this.partnershipProposalRepository = partnershipProposalRepository;
    }

    public UniversityDashboardDTO getUniversityDashboard(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        
        if (universityOpt.isPresent()) {
            University university = universityOpt.get();
            UniversityDashboardDTO dashboard = new UniversityDashboardDTO();
            
            // Get total students
            List<User> students = userRepository.findByUniversity(university);
            dashboard.setTotalStudents(students.size());
            
            // Get active exchanges
            // This would require custom query methods
            dashboard.setActiveExchanges(0L); // Placeholder
            
            // Get top skills
            // This would require complex aggregation queries
            dashboard.setTopSkills(List.of()); // Placeholder
            
            // Get total projects
            // This would require custom query methods
            dashboard.setTotalProjects(0L); // Placeholder
            
            // Get total workshops
            List<Workshop> workshops = workshopRepository.findByHostUniversity(university);
            dashboard.setTotalWorkshops(workshops.size());
            
            // Get upcoming workshops
            List<Workshop> upcomingWorkshops = workshopRepository
                .findByHostUniversityAndDateTimeAfter(university, LocalDateTime.now());
            dashboard.setUpcomingWorkshops(upcomingWorkshops.size());
            
            // Get pending partnership proposals
            List<PartnershipProposal> pendingProposals = partnershipProposalRepository
                .findByTargetUniversityAndStatus(university, PartnershipProposal.ProposalStatus.PENDING);
            dashboard.setPendingPartnershipProposals(pendingProposals.size());
            
            return dashboard;
        }
        
        return null;
    }
}