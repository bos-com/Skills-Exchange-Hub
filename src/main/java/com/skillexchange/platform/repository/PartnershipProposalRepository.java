package com.skillexchange.platform.repository;

import com.skillexchange.platform.entity.PartnershipProposal;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartnershipProposalRepository extends MongoRepository<PartnershipProposal, String> {
    List<PartnershipProposal> findByProposingUniversity(University proposingUniversity);
    List<PartnershipProposal> findByTargetUniversity(University targetUniversity);
    List<PartnershipProposal> findByProposer(User proposer);
    List<PartnershipProposal> findByStatus(PartnershipProposal.ProposalStatus status);
    List<PartnershipProposal> findByProposingUniversityOrTargetUniversity(University university1, University university2);
    List<PartnershipProposal> findByTargetUniversityAndStatus(University targetUniversity, PartnershipProposal.ProposalStatus status);
}