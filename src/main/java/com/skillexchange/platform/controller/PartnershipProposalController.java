package com.skillexchange.platform.controller;

import com.skillexchange.platform.dto.PartnershipProposalDTO;
import com.skillexchange.platform.entity.PartnershipProposal;
import com.skillexchange.platform.service.PartnershipProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@CrossOrigin(origins = "*")
public class PartnershipProposalController {

    @Autowired
    private PartnershipProposalService partnershipProposalService;

    // Create a new partnership proposal (University Admin only)
    @PostMapping
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<PartnershipProposalDTO> createProposal(@Valid @RequestBody PartnershipProposalDTO proposalDTO) {
        PartnershipProposal proposal = partnershipProposalService.createProposal(
            proposalDTO.getTitle(),
            proposalDTO.getDescription(),
            proposalDTO.getProposingUniversityId(),
            proposalDTO.getTargetUniversityId(),
            proposalDTO.getProposerId()
        );
        
        if (proposal != null) {
            return ResponseEntity.ok(partnershipProposalService.convertToDTO(proposal));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Respond to a partnership proposal (University Admin only)
    @PutMapping("/{proposalId}/respond")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<PartnershipProposalDTO> respondToProposal(@PathVariable String proposalId,
                                                                  @RequestParam PartnershipProposal.ProposalStatus status) {
        PartnershipProposal proposal = partnershipProposalService.respondToProposal(proposalId, status);
        
        if (proposal != null) {
            return ResponseEntity.ok(partnershipProposalService.convertToDTO(proposal));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get proposals by university
    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<PartnershipProposalDTO>> getProposalsByUniversity(@PathVariable String universityId) {
        List<PartnershipProposal> proposals = partnershipProposalService.getProposalsByUniversity(universityId);
        return ResponseEntity.ok(partnershipProposalService.convertToDTOList(proposals));
    }

    // Get proposals for university
    @GetMapping("/university/{universityId}/incoming")
    public ResponseEntity<List<PartnershipProposalDTO>> getProposalsForUniversity(@PathVariable String universityId) {
        List<PartnershipProposal> proposals = partnershipProposalService.getProposalsForUniversity(universityId);
        return ResponseEntity.ok(partnershipProposalService.convertToDTOList(proposals));
    }

    // Get proposals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PartnershipProposalDTO>> getProposalsByStatus(@PathVariable PartnershipProposal.ProposalStatus status) {
        List<PartnershipProposal> proposals = partnershipProposalService.getProposalsByStatus(status);
        return ResponseEntity.ok(partnershipProposalService.convertToDTOList(proposals));
    }

    // Get proposals for university by status
    @GetMapping("/university/{universityId}/status/{status}")
    public ResponseEntity<List<PartnershipProposalDTO>> getProposalsForUniversityByStatus(
            @PathVariable String universityId, 
            @PathVariable PartnershipProposal.ProposalStatus status) {
        List<PartnershipProposal> proposals = partnershipProposalService.getProposalsForUniversityByStatus(universityId, status);
        return ResponseEntity.ok(partnershipProposalService.convertToDTOList(proposals));
    }

    // Update a proposal (University Admin only)
    @PutMapping("/{proposalId}")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<PartnershipProposalDTO> updateProposal(@PathVariable String proposalId,
                                                               @Valid @RequestBody PartnershipProposalDTO proposalDTO) {
        PartnershipProposal proposal = partnershipProposalService.updateProposal(
            proposalId,
            proposalDTO.getTitle(),
            proposalDTO.getDescription()
        );
        
        if (proposal != null) {
            return ResponseEntity.ok(partnershipProposalService.convertToDTO(proposal));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a proposal (University Admin only)
    @DeleteMapping("/{proposalId}")
    @PreAuthorize("hasRole('UNIVERSITY_ADMIN')")
    public ResponseEntity<Void> deleteProposal(@PathVariable String proposalId) {
        boolean deleted = partnershipProposalService.deleteProposal(proposalId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}