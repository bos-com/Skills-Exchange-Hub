package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.PartnershipProposalDTO;
import com.skillexchange.platform.entity.PartnershipProposal;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.PartnershipProposalRepository;
import com.skillexchange.platform.repository.UniversityRepository;
import com.skillexchange.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PartnershipProposalService {

    private final PartnershipProposalRepository partnershipProposalRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;

    public PartnershipProposalService(PartnershipProposalRepository partnershipProposalRepository, 
                                    UniversityRepository universityRepository, 
                                    UserRepository userRepository) {
        this.partnershipProposalRepository = partnershipProposalRepository;
        this.universityRepository = universityRepository;
        this.userRepository = userRepository;
    }

    public PartnershipProposal createProposal(String title, String description, 
                                            String proposingUniversityId, String targetUniversityId, 
                                            String proposerId) {
        Optional<University> proposingUniversityOpt = universityRepository.findById(proposingUniversityId);
        Optional<University> targetUniversityOpt = universityRepository.findById(targetUniversityId);
        Optional<User> proposerOpt = userRepository.findById(proposerId);
        
        if (proposingUniversityOpt.isPresent() && targetUniversityOpt.isPresent() && proposerOpt.isPresent()) {
            PartnershipProposal proposal = new PartnershipProposal(
                title, 
                proposingUniversityOpt.get(), 
                targetUniversityOpt.get(), 
                proposerOpt.get()
            );
            proposal.setDescription(description);
            return partnershipProposalRepository.save(proposal);
        }
        
        return null;
    }

    public PartnershipProposal respondToProposal(String proposalId, PartnershipProposal.ProposalStatus status) {
        Optional<PartnershipProposal> proposalOpt = partnershipProposalRepository.findById(proposalId);
        
        if (proposalOpt.isPresent()) {
            PartnershipProposal proposal = proposalOpt.get();
            proposal.setStatus(status);
            proposal.setRespondedAt(LocalDateTime.now());
            return partnershipProposalRepository.save(proposal);
        }
        
        return null;
    }

    public List<PartnershipProposal> getProposalsByUniversity(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        return universityOpt.map(partnershipProposalRepository::findByProposingUniversity)
                .orElse(List.of());
    }

    public List<PartnershipProposal> getProposalsForUniversity(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        return universityOpt.map(partnershipProposalRepository::findByTargetUniversity)
                .orElse(List.of());
    }

    public List<PartnershipProposal> getProposalsByStatus(PartnershipProposal.ProposalStatus status) {
        return partnershipProposalRepository.findByStatus(status);
    }

    public List<PartnershipProposal> getProposalsForUniversityByStatus(String universityId, 
                                                                      PartnershipProposal.ProposalStatus status) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        if (universityOpt.isPresent()) {
            List<PartnershipProposal> proposals = partnershipProposalRepository
                .findByTargetUniversity(universityOpt.get());
            
            return proposals.stream()
                .filter(proposal -> proposal.getStatus() == status)
                .collect(Collectors.toList());
        }
        return List.of();
    }

    public PartnershipProposal updateProposal(String proposalId, String title, String description) {
        Optional<PartnershipProposal> proposalOpt = partnershipProposalRepository.findById(proposalId);
        
        if (proposalOpt.isPresent()) {
            PartnershipProposal proposal = proposalOpt.get();
            if (title != null) proposal.setTitle(title);
            if (description != null) proposal.setDescription(description);
            return partnershipProposalRepository.save(proposal);
        }
        
        return null;
    }

    public boolean deleteProposal(String proposalId) {
        if (partnershipProposalRepository.existsById(proposalId)) {
            partnershipProposalRepository.deleteById(proposalId);
            return true;
        }
        return false;
    }

    public PartnershipProposalDTO convertToDTO(PartnershipProposal proposal) {
        PartnershipProposalDTO dto = new PartnershipProposalDTO();
        dto.setId(proposal.getId());
        dto.setTitle(proposal.getTitle());
        dto.setDescription(proposal.getDescription());
        dto.setProposingUniversityId(proposal.getProposingUniversity().getId());
        dto.setProposingUniversityName(proposal.getProposingUniversity().getName());
        dto.setTargetUniversityId(proposal.getTargetUniversity().getId());
        dto.setTargetUniversityName(proposal.getTargetUniversity().getName());
        dto.setStatus(proposal.getStatus());
        dto.setProposedAt(proposal.getProposedAt());
        dto.setRespondedAt(proposal.getRespondedAt());
        dto.setProposerId(proposal.getProposer().getId());
        dto.setProposerName(proposal.getProposer().getUsername());
        dto.setCreatedAt(proposal.getCreatedAt());
        dto.setUpdatedAt(proposal.getUpdatedAt());
        return dto;
    }

    public List<PartnershipProposalDTO> convertToDTOList(List<PartnershipProposal> proposals) {
        return proposals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}